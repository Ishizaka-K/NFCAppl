package com.example.nfcapplication

import android.content.ContentValues.TAG
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.NfcF
import android.os.Bundle
import android.os.Parcelable
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.io.IOException
import java.nio.ByteBuffer
import java.util.Calendar

class MainActivity : AppCompatActivity() {
    private var tv1: TextView? = null
    private var tv2: TextView? = null
    private val sb1 = StringBuffer()
    private val sb2 = StringBuffer()

    private val MENU_ID1 = Menu.FIRST
    private var cont_flg = 0
    private val idm = ByteArray(8)
    private val edyResult = ByteArray(16 * 8)
    private val polling_common_area_command = byteArrayOf(
        0x06.toByte(),
        0x00.toByte(),
        0xFE.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x0F.toByte()
    )
    private val request_service_edy_no_command = byteArrayOf(
        0x0d.toByte(),
        0x02.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x01.toByte(),
        0x0b.toByte(),
        0x11.toByte()
    )
    private val read_wo_encryption_edy_command = byteArrayOf(
        0x22.toByte(),
        0x06.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x03.toByte(),
        0x0b.toByte(),
        0x11.toByte(),
        0x17.toByte(),
        0x13.toByte(),
        0x0f.toByte(),
        0x17.toByte(),
        0x08.toByte(),
        0x80.toByte(),
        0x00.toByte(),
        0x81.toByte(),
        0x00.toByte(),
        0x82.toByte(),
        0x00.toByte(),
        0x82.toByte(),
        0x01.toByte(),
        0x82.toByte(),
        0x02.toByte(),
        0x82.toByte(),
        0x03.toByte(),
        0x82.toByte(),
        0x04.toByte(),
        0x82.toByte(),
        0x05.toByte()
    )

    /** Called when the activity is first created.  */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val intent = intent
        val tag = intent.putExtra(NfcAdapter.EXTRA_TAG,TAG) as Tag?
        val techF = NfcF.get(tag)
        val button = findViewById<Button>(R.id.button01)
        button.setOnClickListener { System.exit(RESULT_OK) }
        tv1 = findViewById<View>(R.id.TextView01) as TextView
        tv2 = findViewById<View>(R.id.TextView02) as TextView
        var responce: ByteArray? = null
        try {
            techF.connect()
            if (techF.isConnected) {
                responce = techF.transceive(polling_common_area_command)
                for (i in 0..7) {
                    idm[i] = responce[i + 2]
                }
                sb2.append("Polling Sent Command: ")
                sb2.append("\n")
                for (i in polling_common_area_command.indices) {
                    sb2.append(String.format("%02X", polling_common_area_command[i]))
                }
                sb2.append("\n")
                sb2.append("Polling Responce Packet: ")
                sb2.append("\n")
                for (i in responce.indices) {
                    sb2.append(String.format("%02X", responce[i]))
                }
                sb2.append("\n")
                tv2!!.text = sb2
                cont_flg = 1
            } else {
                sb1.append("FeliCaが離れました")
                sb1.append("\n")
                tv1!!.text = sb1
                sb2.append("Polling Error: ")
                sb2.append("\n")
                sb2.append("FeliCaが離れました")
                sb2.append("\n")
                sb2.append("Polling Sent Command: ")
                sb2.append("\n")
                for (i in polling_common_area_command.indices) {
                    sb2.append(String.format("%02X", polling_common_area_command[i]))
                }
                sb2.append("\n")
                tv2!!.text = sb2
                cont_flg = 0
            }
        } catch (e: IOException) {
            sb1.append("FeliCaが離れたか、Edyではありません")
            sb1.append("\n")
            tv1!!.text = sb1
            sb2.append("Polling Error: ")
            sb2.append("\n")
            sb2.append("FeliCaが離れたか、Edyではありません")
            sb2.append("\n")
            sb2.append("Polling Sent Command: ")
            sb2.append("\n")
            var i = 0
            while (i < polling_common_area_command.size) {
                sb2.append(String.format("%02X", polling_common_area_command[i]))
                i++
            }
            sb2.append("\n")
            tv2!!.text = sb2
            cont_flg = 0
        } catch (e: Exception) {
            sb2.append("Polling Exception: ")
            sb2.append("\n")
            sb2.append(e.message)
            sb2.append("\n")
            tv2!!.text = sb2
            cont_flg = 0
        }
        if (cont_flg == 1) {
            var responce2: ByteArray? = null
            for (i in 0..7) {
                request_service_edy_no_command[2 + i] = idm[i]
            }
            try {
                if (techF.isConnected) {
                    responce2 = techF.transceive(request_service_edy_no_command)
                    sb2.append("Request Service Sent Command: ")
                    sb2.append("\n")
                    for (i in request_service_edy_no_command.indices) {
                        sb2.append(String.format("%02X", request_service_edy_no_command[i]))
                    }
                    sb2.append("\n")
                    sb2.append("Request Service Responce Packet: ")
                    sb2.append("\n")
                    for (i in responce2.indices) {
                        sb2.append(String.format("%02X", responce2[i]))
                    }
                    sb2.append("\n")
                    sb2.append("Key Version: ")
                    sb2.append("\n")
                    for (i in 11..12) {
                        sb2.append(String.format("%02X", responce2[i]))
                    }
                    sb2.append("\n")
                    tv2!!.text = sb2
                    if (responce2[11] == 0xFF.toByte() && responce2[12] == 0xFF.toByte()) {
                        sb1.append("Edyではありません")
                        sb1.append("\n")
                        tv1!!.text = sb1
                        sb2.append("Edyではありません")
                        sb1.append("\n")
                        tv2!!.text = sb2
                        cont_flg = 0
                    } else {
                        cont_flg = 1
                    }
                } else {
                    sb1.append("FeliCaが離れました")
                    sb1.append("\n")
                    tv1!!.text = sb1
                    sb2.append("Request Service Error: ")
                    sb2.append("\n")
                    sb2.append("FeliCaが離れました")
                    sb2.append("\n")
                    sb2.append("Request Service Sent Command: ")
                    sb2.append("\n")
                    for (i in request_service_edy_no_command.indices) {
                        sb2.append(String.format("%02X", request_service_edy_no_command[i]))
                    }
                    sb2.append("\n")
                    tv2!!.text = sb2
                    cont_flg = 0
                }
            } catch (e: IOException) {
                sb1.append("FeliCaが離れたか、Edyではありません")
                sb1.append("\n")
                tv1!!.text = sb1
                sb2.append("Request Service Error: ")
                sb2.append("\n")
                sb2.append("FeliCaが離れたか、Edyではありません")
                sb2.append("\n")
                sb2.append("Request Service Sent Command: ")
                sb2.append("\n")
                var i = 0
                while (i < request_service_edy_no_command.size) {
                    sb2.append(String.format("%02X", request_service_edy_no_command[i]))
                    i++
                }
                sb2.append("\n")
                tv2!!.text = sb2
                cont_flg = 0
            } catch (e: Exception) {
                sb2.append("Request Service Exception: ")
                sb2.append("\n")
                sb2.append(e.message)
                sb2.append("\n")
                tv2!!.text = sb2
                cont_flg = 0
            }
        }
        if (cont_flg == 1) {
            var responce3: ByteArray? = null
            for (i in 0..7) {
                read_wo_encryption_edy_command[2 + i] = idm[i]
            }
            try {
                if (techF.isConnected) {
                    responce3 = techF.transceive(read_wo_encryption_edy_command)
                    sb2.append("Read wo encryption Sent Command: ")
                    sb2.append("\n")
                    for (i in read_wo_encryption_edy_command.indices) {
                        sb2.append(String.format("%02X", read_wo_encryption_edy_command[i]))
                    }
                    sb2.append("\n")
                    sb2.append("Read wo encryption Responce Packet: ")
                    sb2.append("\n")
                    for (i in 0..12) {
                        sb2.append(String.format("%02X", responce3[i]))
                    }
                    sb2.append("\n")
                    for (i in 13 until responce3.size) {
                        sb2.append(String.format("%02X", responce3[i]))
                        if ((i - 12) % 16 == 0) {
                            sb2.append("\n")
                        }
                    }
                    sb2.append("\n")
                    sb2.append("Read wo encryption Status flag 1: ")
                    sb2.append(String.format("%02X", responce3[10]))
                    sb2.append("\n")
                    sb2.append("Read wo encryption Status flag 2: ")
                    sb2.append(String.format("%02X", responce3[11]))
                    sb2.append("\n")
                    tv2!!.text = sb2
                    if (!(responce3[10] == 0x00.toByte() && responce3[11] == 0x00.toByte())) {
                        sb1.append("読み出しに何らかのエラーがありました")
                        sb1.append("\n")
                        tv1!!.text = sb1
                        sb2.append("読み出しに何らかのエラーがありました")
                        sb2.append("\n")
                        tv2!!.text = sb2
                        cont_flg = 0
                    } else {
                        for (i in 0 until 16 * 8) {
                            edyResult[i] = responce3[13 + i]
                        }
                        dispEdyResult(edyResult)
                        cont_flg = 1
                    }
                } else {
                    sb1.append("FeliCaが離れました")
                    sb1.append("\n")
                    tv1!!.text = sb1
                    sb2.append("Read wo encryption Error: ")
                    sb2.append("\n")
                    sb2.append("FeliCaが離れました")
                    sb2.append("\n")
                    sb2.append("Read wo encryption Sent Command: ")
                    sb2.append("\n")
                    for (i in read_wo_encryption_edy_command.indices) {
                        sb2.append(String.format("%02X", read_wo_encryption_edy_command[i]))
                    }
                    sb2.append("\n")
                    tv2!!.text = sb2
                    cont_flg = 0
                }
            } catch (e: IOException) {
                sb2.append("Read wo encryption Error: ")
                sb2.append("\n")
                sb2.append("FeliCaが離れました")
                sb2.append("\n")
                sb2.append("Request Service Sent Command: ")
                sb2.append("\n")
                var i = 0
                while (i < read_wo_encryption_edy_command.size) {
                    sb2.append(String.format("%02X", read_wo_encryption_edy_command[i]))
                    i++
                }
                sb2.append("\n")
                tv2!!.text = sb2
                cont_flg = 0
            } catch (e: Exception) {
                sb2.append("Read wo encryption Exception: ")
                sb2.append("\n")
                sb2.append(e.message)
                sb2.append("\n")
                tv2!!.text = sb2
                cont_flg = 0
            }
        }
        try {
            techF.close()
        } catch (e: IOException) {
            sb2.append("close IOException: ")
            sb2.append("\n")
            sb2.append(e.message)
            sb2.append("\n")
            tv2!!.text = sb2
        } catch (e: Exception) {
            sb2.append("close Exception: ")
            sb2.append("\n")
            sb2.append(e.message)
            sb2.append("\n")
            tv2!!.text = sb2
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val ret = super.onCreateOptionsMenu(menu)
        menu.add(0, MENU_ID1, Menu.NONE, "終了")
        return ret
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            MENU_ID1 -> {
                System.exit(RESULT_OK)
                return true
            }

            else -> {}
        }
        return super.onOptionsItemSelected(item)
    }

    fun dispEdyResult(EdyResult: ByteArray) {
        sb1.append("Edy番号: ")
        for (i in 2..9) {
            sb1.append(String.format("%02X", EdyResult[i]))
        }
        sb1.append("\n")
        sb1.append("Edy残高: ")
        val bf = ByteBuffer.allocate(4)
        bf.put(EdyResult[19])
        bf.put(EdyResult[18])
        bf.put(EdyResult[17])
        bf.put(EdyResult[16])
        sb1.append(String.format("%05d", bf.getInt(0)))
        sb1.append("円")
        sb1.append("\n")
        sb1.append("\n")
        sb1.append("　　　　 日時　　　　, 種別 , 　金額　 , 　残高\n")
        for (i in 0..5) {
            val EdyHistory = ByteArray(16)
            for (j in 0..15) {
                EdyHistory[j] = EdyResult[16 * (i + 2) + j]
            }
            dispEdyHistory(EdyHistory)
            sb1.append("\n")
        }
        tv1!!.text = sb1
    }

    fun dispEdyHistory(EdyHistory: ByteArray) {

        // 日時
        val timebf = ByteBuffer.allocate(4)
        timebf.put(EdyHistory[4])
        timebf.put(EdyHistory[5])
        timebf.put(EdyHistory[6])
        timebf.put(EdyHistory[7])
        val timebfDiff = timebf.getInt(0)
        val dateDiff = timebfDiff ushr 17
        val timeDiff = timebfDiff shl 15 ushr 15
        val historyDate = Calendar.getInstance()
        historyDate[2000, 0, 1, 0, 0] = 0
        historyDate.add(Calendar.DATE, dateDiff)
        sb1.append(String.format("%04d", historyDate[Calendar.YEAR]))
        sb1.append("/")
        sb1.append(String.format("%02d", historyDate[Calendar.MONTH] + 1))
        sb1.append("/")
        sb1.append(String.format("%02d", historyDate[Calendar.DATE]))
        sb1.append(", ")
        val historyTime = Calendar.getInstance()
        historyTime[2000, 0, 1, 0, 0] = 0
        historyTime.add(Calendar.SECOND, timeDiff)
        sb1.append(String.format("%02d", historyTime[Calendar.HOUR_OF_DAY]))
        sb1.append(":")
        sb1.append(String.format("%02d", historyTime[Calendar.MINUTE]))
        sb1.append(":")
        sb1.append(String.format("%02d", historyTime[Calendar.SECOND]))
        sb1.append(", ")

        // 処理通番
        //for(int i = 1; i <= 3; i++) {
        //	sb1.append(String.format("%02X",EdyHistory[i]));
        //}
        //sb1.append(", ");

        // 処理種別
        if (EdyHistory[0] == 0x20.toByte()) {
            sb1.append("支払  ")
        } else if (EdyHistory[0] == 0x02.toByte()) {
            sb1.append("ﾁｬｰｼﾞ")
        } else if (EdyHistory[0] == 0x04.toByte()) {
            sb1.append("ｷﾞﾌﾄ  ")
        } else {
            sb1.append("不明  ")
        }
        sb1.append(", ")

        // 金額
        val bf1 = ByteBuffer.allocate(4)
        bf1.put(EdyHistory[8])
        bf1.put(EdyHistory[9])
        bf1.put(EdyHistory[10])
        bf1.put(EdyHistory[11])
        sb1.append(String.format("%05d", bf1.getInt(0)))
        sb1.append("円")
        sb1.append(", ")

        // 残高
        val bf2 = ByteBuffer.allocate(4)
        bf2.put(EdyHistory[12])
        bf2.put(EdyHistory[13])
        bf2.put(EdyHistory[14])
        bf2.put(EdyHistory[15])
        sb1.append(String.format("%05d", bf2.getInt(0)))
        sb1.append("円")
    }


//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//    }
}