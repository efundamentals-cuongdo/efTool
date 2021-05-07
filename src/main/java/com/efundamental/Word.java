package com.efundamental;

import java.util.List;

public class Word {
    private String word;
    private String url;

    public String getUrl() {
        return url;
    }

    public String getWord() {
        return word;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public boolean checkExist(String word, List<Word> lst) {
        for (Word word2 : lst) {
            if (word.equals(word2.getWord())) {
                return true;
            }
        }
        return false;
    }
}
