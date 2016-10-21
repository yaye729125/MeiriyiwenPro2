package net.gyapp.meiriyiwenpro2;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import org.jsoup.Jsoup;
import org.jsoup.examples.HtmlToPlainText;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xml.sax.XMLReader;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = MainActivity.class.getSimpleName();

    @InjectView(R.id.htmlTv)
    TextView htmlTextView;
    //@InjectView(R.id.toolbar)
    Toolbar toolbar;
    ExecutorService executorService = Executors.newSingleThreadExecutor();

    //主页地址
    static final String HomePageUrl = "http://www.meiriyiwen.com";
    //随机页地址
    static final String RandomPageUrl = "http://meiriyiwen.com/random";
    //请求数据地址
    String PostUrl = HomePageUrl;

    //前一次点击时间
    long lastHtmlClickTime = 0;
    //前一次响应双击的时间
    long lastDubbleTime = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
//        Toolbar toolbar =//new Toolbar(this);
        //getSupportActionBar()
        ButterKnife.inject(this);
        //setSupportActionBar(toolbar);
        //toolbar.setOnCreateContextMenuListener(this);
        //htmlTextView.setTextIsSelectable(true);
        new AsyncParseTask().executeOnExecutor(executorService);

    }

    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        // Handle action buttons
        switch (item.getItemId()) {
            case R.id.share:
                // create intent to perform web search for this planet
//                Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
//                intent.putExtra(SearchManager.QUERY, getSupportActionBar().getTitle());
//                // catch event that there's no activity to handle intent
//                if (intent.resolveActivity(getPackageManager()) != null) {
//                    startActivity(intent);
//                } else {
//                    Toast.makeText(this, R.string.app_not_available, Toast.LENGTH_LONG).show();
//                }
                onShare();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    public void onShare() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        //intent.putExtra(Intent.EXTRA_SUBJECT,"share");
        //intent.putExtra(Intent.Extra_ti),
        intent.putExtra(Intent.EXTRA_TEXT, Html.toHtml((Spanned) htmlTextView.getText()));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(Intent.createChooser(intent, "分享该文章"));
    }

    public void onHtmlClick(View v) {
        long now = System.currentTimeMillis();
        if (now - lastHtmlClickTime < 900) {

            //两次点击时间差小于900ms为双击
            if (now - lastDubbleTime > 900) {
                //过滤连续点击,两次双击时间差大于900ms为有效
                getRandomArticle();
                lastDubbleTime = now;
            }
            lastHtmlClickTime = now;
        } else {
            lastHtmlClickTime = now;
        }
    }

    /**
     * 获取随机文章
     */
    private void getRandomArticle() {
        PostUrl = RandomPageUrl;
        new AsyncParseTask().executeOnExecutor(executorService);
    }


    /**
     * 请求及解析数据
     *
     * @return
     */
    public Element parseDoc() {
        try {
            Document doc = Jsoup.parse(new URL(PostUrl), 10 * 1000);
            System.out.println(doc.toString());
            //Log.i("MainActivity","content******* \n " + doc.toString());
            Elements elements;
            if (PostUrl.equals(HomePageUrl)) {
                elements = doc.select("div.container");
                //doc.getElementById("article_show");

                return elements.size() > 0 ? elements.get(0) : null;

            } else {
                Element article_show = doc.getElementById("article_show");
//                Element article_text = article_show.getElementsByClass("article_text").first();
//                Elements elements1 = article_text.getAllElements();
//                //Elements tempElements = new Elements();
//                for (int i = 0; i < elements1.size(); i++) {
//
//                    if (i == 0 || i == (elements1.size() -1)){
//                        elements1.get(i).remove();
//                        //continue;
//                    }
//                    //tempElements.add(elements1.get(i));
//                }
                //elements1.clear();
                //elements1.addAll(tempElements);
                //article_text.remove();
                //article_show.replaceWith();

                //article_show.
                return article_show;
            }
//            for (Element element:
//                    elements) {
//                //mHandler.obtainMessage(HandlerWhat,element.toString()).sendToTarget();
//            }
            // if (elements.size() > 0)
            //return elements.first();

            //mHandler.post(new Run)
//                MainActivity.this.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//
//                    }
//                });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public class AsyncParseTask extends AsyncTask<String, Void, Element> {
        Html.TagHandler tagHandler = new Html.TagHandler() {
            @Override
            public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {

            }
        };
        Html.ImageGetter imageGetter = new Html.ImageGetter() {
            @Override
            public Drawable getDrawable(String source) {
                return null;
            }
        };

        @Override
        protected Element doInBackground(String... params) {

            return parseDoc();
        }

        public AsyncParseTask() {
            super();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.i(TAG, "parseTask  onPreExecute!");
        }

        @Override
        protected void onPostExecute(Element element) {
            super.onPostExecute(element);
            Log.i(TAG, "parseTask  onPostExecute!");
            if (element != null) {
                //Html.fromHtml()
                Spanned spanned;//=  Html.fromHtml(element.toString(),Html.FROM_HTML_SEPARATOR_LINE_BREAK_PARAGRAPH);
                spanned = Html.fromHtml(element.toString());

                htmlTextView.setText(spanned);
            }
            //htmlTextView.append(element.toString());
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            Log.i(TAG, "parseTask update progress!");
        }

        @Override
        protected void onCancelled(Element element) {
            super.onCancelled(element);
            Log.i(TAG, "parseTask cancled! with argument");
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            Log.i(TAG, "parseTask cancled!");
        }
    }


    static final int HandlerWhat = 0x1001;

    static Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HandlerWhat:
                    //htmlTextView.setText((String)msg.obj);
                    break;
                default:
                    break;
            }
        }
    };

    /**
     *
     */
    public static class ParseTask extends Thread {

        public void parseDoc() {
            try {
                Document doc = Jsoup.parse(new URL("http://www.meiriyiwen.com"), 10 * 1000);

                Log.i("MainActivity", "content******* \n " + doc.toString());
                Elements elements = doc.select("div.");
                for (Element element :
                        elements) {
                    mHandler.obtainMessage(HandlerWhat, element.toString()).sendToTarget();
                }
                //mHandler.post(new Run)
//                MainActivity.this.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//
//                    }
//                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {
            parseDoc();
        }
    }
}
