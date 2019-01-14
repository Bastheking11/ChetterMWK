package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.gson.Gson;
import domain.entity.Message;
import domain.viewmodel.MessageView;
import domain.viewmodel.ViewModel;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;

import static java.lang.Math.toIntExact;

@ServerEndpoint(APIController.api_path + "/party/{pid}/channel/{cid}/socket")
public class MessageSocketController {

    private static Map<Integer, List<Session>> sessions = new HashMap<>();

    @OnOpen
    public void onOpen(
            @PathParam("pid") Long pid,
            @PathParam("cid") Long cid,
            Session session
    ) throws IOException {
        int hash = toIntExact(cid);
        addSession(hash, session);
    }

    private static void addSession(int key, Session session) {
        sessions.putIfAbsent(key, new ArrayList<>());
        sessions.get(key).add(session);
    }

    private static void removeSession(Session session) {
        int key = MessageSocketController.findSessionKey(session);
        if (key < 0) return;

        sessions.get(key).remove(session);

        if (sessions.get(key).isEmpty())
            sessions.remove(key);
    }

    @OnMessage
    public void onMessage(Session session, String channel) throws IOException {
        int cid = Integer.valueOf(channel);
        removeSession(session);
        addSession(cid, session);
    }

    @OnClose
    public static void onClose(Session session) throws IOException {
        removeSession(session);
    }

    private static int findSessionKey(Session session) {
        for (Map.Entry<Integer, List<Session>> all : sessions.entrySet()) {
            if (all.getValue().contains(session)) {
                return all.getKey();
            }
        }
        return -1;
    }

    static void messageToChannel(Message message) {
        try {
            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            String json = ow.writeValueAsString(ViewModel.Convert(message, MessageView.class));

            int hash = toIntExact(message.getChannel().getId());

            if (!sessions.containsKey(hash))
                return;

            for (Session s : sessions.get(hash)) {
                if (s.isOpen())
                    s.getBasicRemote().sendObject(json);
                else
                    onClose(s);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
