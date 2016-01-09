/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.misaki.gomoku.server.auth;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.websocket.Session;

/**
 *
 * @author vlumi
 */
public class User implements Serializable {

    private static final long serialVersionUID = -3454841459380520035L;

    /**
     *
     */
    private String name = "";
    /**
     *
     */
    private String passwordHash = "";
    /**
     *
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
        this.sessions.add(session);
        System.out.println("Added session, now: " + this.sessions);
    }

    public void removeSession(Session session) {
        if (this.sessions.contains(session)) {
            this.sessions.remove(session);
            System.out.println("Removed session, now: " + this.sessions);
        }
    }

}
