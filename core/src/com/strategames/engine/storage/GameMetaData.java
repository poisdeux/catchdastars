package com.strategames.engine.storage;

import java.util.HashMap;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.strategames.engine.gameobject.types.Door;
import com.strategames.engine.utils.Level;

public class GameMetaData implements Json.Serializable, Writer {
	private String uuid;
	private String name;
	private String designer;
    private HashMap<String, String> additionalInfo = new HashMap<String, String>();

	public GameMetaData() {
		this.uuid = UUID.randomUUID().toString();
	}

	@Override
	public void write(Json json) {
		json.writeValue("uuid", uuid);
		json.writeValue("name", name);
		json.writeValue("designer", designer);
        json.writeValue("info", convertAdditionalInfoHashToString());
	}

//    /**
//     * Do not override this method in a subclass but override {@link #readValue(com.badlogic.gdx.utils.JsonValue)}
//     * to read json values in a subclass
//     * @param json
//     * @param jsonData
//     */
	@Override
	public void read(Json json, JsonValue jsonData) {
		JsonValue child = jsonData.child();

		while( child != null ) {

			if(child.name.contentEquals("uuid")) {
				uuid = child.asString();
			} else if(child.name.contentEquals("name")) {
				name = child.asString();
			} else if(child.name.contentEquals("designer")) {
                designer = child.asString();
            } else if(child.name.contentEquals("info")) {
                parseAdditionalInfo(child.asString());
            }

			child = child.next();
		}
	}

    public void setAdditionalInfo(String name, String value) {
        this.additionalInfo.put(name, value);
    }

    public String getAdditionalInfo(String name) {
        return additionalInfo.get(name);
    }

    public String getUuid() {
		return uuid;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setDesigner(String designer) {
		this.designer = designer;
	}

	public String getDesigner() {
		return designer;
	}
	
	public String getJson() {
		Json json = new Json();
		json.setOutputType(OutputType.minimal);
		return json.toJson(this);
	}

	@Override
	public String getFilename() {
		return "meta";
	}

//	@Override
//	public String toString() {
//		return super.toString() + ", uuid="+this.uuid+", name="+this.name+
//				", designer="+designer+", currentLevelPosition="+this.currentLevelPosition+
//				", score="+this.score;
//	}

    @Override
    public boolean equals(Object obj) {
        if( obj == null ) {
            return false;
        }

        if( this == obj ) {
            return true;
        }

        if( ! ( obj instanceof GameMetaData) ) {
            return false;
        }

        GameMetaData gameMetaData = (GameMetaData) obj;

        if( ! compareStrings(this.uuid, gameMetaData.uuid) ) {
            return false;
        }

        if( ! compareStrings(this.name, gameMetaData.name) ) {
            return false;
        }

        if( ! compareStrings(this.designer, gameMetaData.designer) ) {
            return false;
        }

//        if( ( this.currentLevelPosition[0] != game.currentLevelPosition[0] ) ||
//                ( this.currentLevelPosition[1] != game.currentLevelPosition[1] ) ) {
//            return false;
//        }
//
//        if( this.score != game.score ) {
//            return false;
//        }

        return true;
    }

    private boolean compareStrings(String s1, String s2) {
        if( s1 != null ) {
            if( ! s1.contentEquals(s2) ) {
                return false;
            }
        } else {
            if ( s2 != null ) {
                return false;
            }
        }
        return true;
    }

    private void parseAdditionalInfo(String info) {
        String[] elts = info.split(":");
        for( int i = 0; i < elts.length; i += 2 ) {
            String key = elts[i];
            String value = elts[i+1];
            this.additionalInfo.put(key, value);
        }
    }

    private String convertAdditionalInfoHashToString() {
        StringBuffer stringBuffer = new StringBuffer();
        Set<String> keys = additionalInfo.keySet();
        for( String key : keys ) {
            stringBuffer.append(key + ":" + additionalInfo.get(key));
        }
        return stringBuffer.toString();
    }
}
