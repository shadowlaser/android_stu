package guessmusic.imooc.studio.com.guessmusicstu;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class Activity01 extends Activity {
    Button btn=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_01);
    btn =(Button)findViewById(R.id.switch1);
        btn.setOnClickListener(lsr);
    }

    private View.OnClickListener lsr=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            new AlertDialog.Builder(Activity01.this).setTitle("这是标题")
                    .setMessage("这是中间的内容")
                    .setPositiveButton("确定",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent it=new Intent();
                            it.setClass(Activity01.this,Activity02.class);
                            Activity01.this.startActivity(it);
                        }
                    })
                    .setNegativeButton("取消",null)
                    .show();
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity01, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
