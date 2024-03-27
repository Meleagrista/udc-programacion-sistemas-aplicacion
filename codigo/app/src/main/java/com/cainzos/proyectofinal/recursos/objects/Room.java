package com.cainzos.proyectofinal.recursos.objects;

public class Room {
    private String roomId;
    private String gameData;

    public Room(String roomId, String gameData) {
        this.roomId = roomId;
        this.gameData = gameData;
    }

    public String getRoomId() {
        return roomId;
    }

    public String getGameData() {
        return gameData;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public void setGameData(String gameData) {
        this.gameData = gameData;
    }
}
