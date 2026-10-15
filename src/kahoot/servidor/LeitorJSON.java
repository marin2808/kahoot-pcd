package kahoot.servidor;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

public class LeitorJSON {
	
	public static List<PerguntaServidor> lerJson(String ficheiro){
		JsonReader leitor;
		try{
			leitor = new JsonReader(new FileReader(ficheiro));
			JsonObject root = JsonParser.parseReader(leitor).getAsJsonObject();
			JsonArray perguntasArray = root.getAsJsonArray("questions");
			Gson gson = new Gson();
			Type perguntasLista =  new TypeToken<List<PerguntaServidor>>(){}.getType();
			List<PerguntaServidor> perguntas = gson.fromJson(perguntasArray, perguntasLista);
			return perguntas;
		} catch (FileNotFoundException e) {
			System.out.println("Ficheiro não encontrado!");
			return null;
		}
		
	}

}
