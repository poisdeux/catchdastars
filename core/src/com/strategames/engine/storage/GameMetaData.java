package com.strategames.engine.storage;

import java.util.HashMap;
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
//	private int score;
    private String additionalInfo;

	public GameMetaData() {
		this.uuid = UUID.randomUUID().toString();
	}

	@Override
	public void write(Json json) {
		json.writeValue("uuid", uuid);
		json.writeValue("name", name);
		json.writeValue("designer", designer);
        json.writeValue("info", additionalInfo);
//		json.writeValue("score", score);
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
                additionalInfo = child.asString();
            }
//			} else if(child.name.contentEquals("currentLevelPosition")) {
//                try {
//                    currentLevelPosition = child.asIntArray();
//                } catch ( IllegalStateException e ) {
//                    Gdx.app.log("Game", "read: currentLevelPosition value is not an array");
//                    currentLevelPosition[0] = 0;
//                    currentLevelPosition[1] = 0;
//                 }
//			} else if(child.name.contentEquals("score")) {
//				score = child.asInt();
//			} else {
//                readValue(child);
//            }
			child = child.next();
		}
	}

//	public int getScore() {
//		return score;
//	}
//
//	public void setScore(int score) {
//		this.score = score;
//	}
//
//	public int[] getCurrentLevelPosition() {
//		return currentLevelPosition;
//	}
//
//	public void setCurrentLevelPosition(int[] currentLevelPosition) {
//		this.currentLevelPosition = currentLevelPosition;
//	}


    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
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
}
