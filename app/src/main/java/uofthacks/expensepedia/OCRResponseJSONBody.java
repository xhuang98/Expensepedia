package uofthacks.expensepedia;

import java.util.List;

public class OCRResponseJSONBody {
    private String language;
    private String textAngle;
    private String orientation;
    private List<Regions> regions;

    /**
     * @return the language
     */
    public String getLanguage() {
        return language;
    }

    /**
     * @param language the language to set
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * @return the textAngle
     */
    public String getTextAngle() {
        return textAngle;
    }

    /**
     * @param textAngle the textAngle to set
     */
    public void setTextAngle(String textAngle) {
        this.textAngle = textAngle;
    }

    /**
     * @return the orientation
     */
    public String getOrientation() {
        return orientation;
    }

    /**
     * @param orientation the orientation to set
     */
    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }

    /**
     * @return the regions
     */
    public List<Regions> getRegions() {
        return regions;
    }

    /**
     * @param regions the regions to set
     */
    public void setRegions(List<Regions> regions) {
        this.regions = regions;
    }

}

class Regions{
    private String boundingBox;
    private List<Lines> lines;

    /**
     * @return the boundingBox
     */
    public String getBoundingBox() {
        return boundingBox;
    }

    /**
     * @param boundingBox the boundingBox to set
     */
    public void setBoundingBox(String boundingBox) {
        this.boundingBox = boundingBox;
    }

    /**
     * @return the lines
     */
    public List<Lines> getLines() {
        return lines;
    }

    /**
     * @param lines the lines to set
     */
    public void setLines(List<Lines> lines) {
        this.lines = lines;
    }


}

class Lines{
    private String boundingBox;
    private List<Words> words;

    /**
     * @return the boundingBox
     */
    public String getBoundingBox() {
        return boundingBox;
    }

    /**
     * @param boundingBox the boundingBox to set
     */
    public void setBoundingBox(String boundingBox) {
        this.boundingBox = boundingBox;
    }

    /**
     * @return the words
     */
    public List<Words> getWords() {
        return words;
    }

    /**
     * @param words the words to set
     */
    public void setWords(List<Words> words) {
        this.words = words;
    }

}

class Words{
    private String boundingBox;
    private String text;

    /**
     * @return the boundingBox
     */
    public String getBoundingBox() {
        return boundingBox;
    }

    /**
     * @param boundingBox the boundingBox to set
     */
    public void setBoundingBox(String boundingBox) {
        this.boundingBox = boundingBox;
    }

    /**
     * @return the text
     */
    public String getText() {
        return text;
    }

    /**
     * @param text the text to set
     */
    public void setText(String text) {
        this.text = text;
    }

}
