package com.strategames.engine.storage;

/**
 * Created by martijn on 1-1-15.
 */
public interface Writer {
    /**
     * Must return the json data to be written
     *
     * @return String containing the json data
     */
    public String getJson();

    /**
     * Must return the filename where the json data should be written to.
     *
     * @return String filename
     */
    public String getFilename();
}
