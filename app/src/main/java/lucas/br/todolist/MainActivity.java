package lucas.br.todolist;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends Activity {

    private EditText                textoTarefa;
    private Button                  botaoAddd;
    private ListView                listaTarefas;
    private SQLiteDatabase          bancoDados;
    private ArrayAdapter<String>    itensAdapter;
    private ArrayList<String>       itens;
    private ArrayList<Integer> ids;

    //VARIAVEL STATICA PARA A TABELA
    private static final String     TABELA_TAREFA = "tarefas";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


            //RECUPERA COMPONENTES
            listaTarefas     = findViewById(R.id.listaId);
            textoTarefa      = findViewById(R.id.campoTarefaId);
            botaoAddd        = findViewById(R.id.btnAddId);

            //SETA O LISTENER NO BOTÃO "ADICIONAR"
            botaoAddd.setOnClickListener(listener_btnAdd);
            listaTarefas.setLongClickable(true);
            listaTarefas.setOnItemLongClickListener(listener_item_lista);

            //RECUPERA AS TAREFAS
            recuperaTarefas();

            //CRIA BANCO DE DADOS
            bancoDados = openOrCreateDatabase("apptarefas", MODE_PRIVATE, null);

            //CRIA AS TABELAS DO BANDO DE DADOS
            bancoDados.execSQL("DROP TABLE IF EXISTS    " + TABELA_TAREFA );
            bancoDados.execSQL("CREATE TABLE IF NOT EXISTS " + TABELA_TAREFA + "(id INTEGER PRIMARY KEY AUTOINCREMENT, tarefa VARCHAR)");

    }

    //LISTENER DOS ITENS DA LISTA COM TOQUE LONGO
    private AdapterView.OnItemLongClickListener listener_item_lista =  new AdapterView.OnItemLongClickListener() {
        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
            removeTarefa(ids.get(position));
            return true;
        }
    };


    //LISTENER DO BOTÃO ADICIONAR
    private View.OnClickListener listener_btnAdd = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

                String textoDigitado = textoTarefa.getText().toString();
                salvarTarefa(textoDigitado);
        }
    };

    //METODO PARA SALVAR AS TAREFAS
    private void salvarTarefa( String texto) {

        try {
            if (texto.equals("")){
                Toast.makeText(this, "Digite uma tarefa", Toast.LENGTH_SHORT).show();

            }else {
                bancoDados.execSQL("INSERT INTO "+ TABELA_TAREFA + "(tarefa) VALUES('" + texto + "')");
                textoTarefa.setText("");
                recuperaTarefas();

            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void recuperaTarefas(){
        try {

            //CRIANDO CURSOR PARA PERCORRER A TABELA E RECUPERAR AS TAREFAS
            Cursor cursor = bancoDados.rawQuery("SELECT * FROM "+ TABELA_TAREFA +" ORDER BY id DESC ", null);
            cursor.moveToFirst();

            //RECUPERA OS IDS DAS TAREFAS
            int indiceColunaId      = cursor.getColumnIndex("id");
            int indiceColunaTarefa  = cursor.getColumnIndex("tarefa");


            //CRIA ADAPTADOR
            ids                 = new ArrayList     <Integer>();
            itens               = new ArrayList     <String>();
            itensAdapter        = new ArrayAdapter  <String>(
                    getApplicationContext()
                    ,android.R.layout.simple_list_item_2
                    ,android.R.id.text2
                    ,itens);

            //SETAR O ADAPTADOR NA LISTA DE ITENS
            listaTarefas.setAdapter(itensAdapter);

            while (cursor != null) {

                itens.add(cursor.getString(indiceColunaTarefa));
                ids.add(Integer.parseInt(cursor.getString(indiceColunaId)));
                cursor.moveToNext();
            }

        }catch (Exception e) {

            e.printStackTrace();
        }
    }

    private void removeTarefa(Integer id){
        try {

            bancoDados.execSQL("DELETE FROM " +TABELA_TAREFA + " WHERE id = "+ id);
            recuperaTarefas();

        }catch (Exception e){

            e.printStackTrace();
        }
    }

}
