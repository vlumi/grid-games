/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.misaki.gomoku.server.user;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;
import javax.websocket.Session;

/**
 *
 * @author vlumi
 */
public class User implements Serializable {

    private static final long serialVersionUID = -3454841459380520035L;

    private static final Logger LOGGER = Logger.getLogger(User.class.getName());

    /**
     * The name of the user, unique across all users.
     */
    private String name = "";
    /**
     * (Optional) hashed password, to allow the same user to be connected from
     * multiple simultaneous clients.
     */
    private String passwordHash = "";
    /**
     * All active sessions for the user, when the user is connected from
     * multiple clients simultaneously.
     */
    private final Set<Session> sessions = Collections.synchronizedSet(new HashSet<>());

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Set<Session> getSessions() {
        return sessions;
    }

    public void addSession(Session session) {
        synchronized (this.sessions) {
            this.sessions.add(session);
            System.out.println("Added session, now: " + this.sessions);
        }
    }

    public void removeSession(Session session) {
        synchronized (this.sessions) {
            if (this.sessions.contains(session)) {
                this.sessions.remove(session);
                System.out.println("Removed session, now: " + this.sessions);
            }
        }
    }

}
