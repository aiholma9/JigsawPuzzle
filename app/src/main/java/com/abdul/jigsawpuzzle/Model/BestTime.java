package com.abdul.jigsawpuzzle.Model;

public class BestTime {

    public int timeElapsed;
    public String difficulty;
    public String puzzleName;
    public String ratio;
    public String pieces;

    public BestTime() {
    }

    public BestTime(int timeElapsed, String difficulty, String puzzleName, String ratio, String pieces) {
        this.timeElapsed = timeElapsed;
        this.difficulty = difficulty;
        this.puzzleName = puzzleName;
        this.ratio = ratio;
        this.pieces = pieces;
    }

    public int getTimeElapsed() {
        return timeElapsed;
    }

    public void setTimeElapsed(int timeElapsed) {
        this.timeElapsed = timeElapsed;
    }

    public String getPieces() {
        return pieces;
    }

    public void setPieces(String pieces) {
        this.pieces = pieces;
    }

    public String getRatio() {
        return ratio;
    }

    public void setRatio(String ratio) {
        this.ratio = ratio;
    }


    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getPuzzleName() {
        return puzzleName;
    }

    public void setPuzzleName(String puzzleName) {
        this.puzzleName = puzzleName;
    }
}
