package com.example.vb.kotlintestservice

import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {


        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)

            btn.setOnClickListener {

              //  val url ="https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20weather.forecast%20where%20woeid%3D2502265&format=json&diagnostics=true&callback="

                val url = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20weather.forecast%20where%20woeid%20in%20(select%20woeid%20from%20geo.places(1)%20where%20text%3D%22nome%2C%20ak%22)&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys"
                CustomAsync().execute(url)

            }
        }

    inner class CustomAsync: AsyncTask<String, String, String>() {


        //inner classlar üst sınıflarından değerleri almak için kullandığımz yapı



        override fun doInBackground(vararg params: String?): String {


            try {

                //execute ettiğimiz paremeteriyi url olarak veriyoruz
                val url=URL(params[0])

                //connectionları açıyoruz
                val urlConnect = url.openConnection() as HttpURLConnection

                //zaman aşımı durumuna karşı süreyi belirleme işlemi
                urlConnect.connectTimeout=7000

                //olayın döndüğü kısım aslında burada bizim urlmizi istediğimiz formata çeviriyor
                var inString = ConvertStreamToString(urlConnect.inputStream)


                //publish progress diyerek onProgresUpdate ye değerimizi yolluyoruz
                publishProgress(inString)

            }catch (ex:Exception){}
            return  " "
        }

        override fun onProgressUpdate(vararg values: String?) {
            try {
                //gelen değerden json olduğundan bir object olarka yaklaşıyoruz
                var json = JSONObject(values[0])

                //ben title istediğimden öcne query sonra results en son
                val query = json.getJSONObject("query")
                val results = query.getJSONObject("results")

                val channel = results.getJSONObject("channel")


                //channelin altında olduğundan title string olarak topladık
                var title = channel.getString("title")

                //gelen değerimizide title değerini tv set ediyoruz
               tv_last.setText("count: "+title)


            }catch (ex:Exception){}
        }

        private fun  ConvertStreamToString(inputStream: InputStream): String {

            //bizim verdiğimiz url üzerinden okumamız için bir diziye dönüştürüyor string
            val _bufferReader = BufferedReader(InputStreamReader(inputStream))


            //satır satır okumak
            var line:String
            //bu satırları biryerde toplamak için işlemlerimiz
            var AllString:String=""

            try {

                do {
                    //line gelen değerleri alıp eğer boş veya null değil ise topluya  yazdıyor
                    line=_bufferReader.readLine()
                    if (line!=null) {
                        AllString += line
                    }


                }while (line!=null)

                //döngü satır null olduğunda bitiyor ve akışı kapatıyoruz
                inputStream.close()
            }catch (ex:Exception){}

            //fonksiyon son olarak değerleri döndürüp ömrünü tamamlıyor
    return AllString

        }


    }
}
