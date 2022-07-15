package tasks;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class EpicAdapter extends TypeAdapter<Epic> {

    @Override
    public void write(JsonWriter out, Epic epic) throws IOException {
        out.beginObject();
        out.name("id");
        out.value(epic.getId());
        out.name("name");
        out.value(epic.getName());
        out.name("description");
        out.value(epic.getDescription());
        out.name("status");
        out.value(epic.getStatus().toString());
        out.endObject();
    }

    @Override
    public Epic read(JsonReader in) throws IOException {
        String name = null;
        String description = null;
        Status status = Status.NEW;
        int id = 0;
        String fieldname = "";

        in.beginObject();
        while (in.hasNext()) {
            JsonToken token = in.peek();

            if (token.equals(JsonToken.NAME)) {
                fieldname = in.nextName();
            }

            switch (fieldname) {
                case "id":
                    id = in.nextInt();
                    break;
                case "name":
                    name = in.nextString();
                    break;
                case "description":
                    description = in.nextString();
                    break;
                case "status":
                    status = Status.valueOf(in.nextString());
                    break;
                default:
                    in.skipValue();
            }
        }
        in.endObject();
        return new Epic(id, name, description, status);
    }
}

