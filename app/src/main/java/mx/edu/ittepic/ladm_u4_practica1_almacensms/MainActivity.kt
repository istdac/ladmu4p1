package mx.edu.ittepic.ladm_u4_practica1_almacensms

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.SmsManager
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import com.google.firebase.firestore.FirebaseFirestore
import mx.edu.ittepic.ladm_u4_practica1_almacensms.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    var manager = SmsManager.getDefault()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mostrarTodo()
        //Obtener sms man
        //var manager = getSystemService(SmsManager::class.java)
        println("Permiso "+(PermissionChecker.checkSelfPermission(this,"android.permission.SEND_SMS")==PermissionChecker.PERMISSION_GRANTED))
        binding.btnEnviar.setOnClickListener {
            if(binding.txtTel.text.isEmpty()){
                AlertDialog.Builder(this).setTitle("Error")
                    .setMessage("Ingrese un número de teléfono")
                    .setNeutralButton("OK"){d,i->}
                    .show()
                return@setOnClickListener
            }
            if(binding.txtMen.text.isEmpty()){
                AlertDialog.Builder(this).setTitle("Error")
                    .setMessage("Ingrese un mensaje")
                    .setNeutralButton("OK"){d,i->}
                    .show()
                return@setOnClickListener
            }
           // if(PermissionChecker.checkSelfPermission(this,"android.permission.SEND_SMS")==PermissionChecker.PERMISSION_GRANTED){
                enviarSMS(binding.txtTel,binding.txtMen)
            //}

        }//clickList
    }//onCreate
    fun enviarSMS(telefono:EditText,mensaje:EditText){
        manager.sendTextMessage(telefono.text.toString(),null,mensaje.text.toString(),null,null)
        insertarFB(telefono.text.toString(),mensaje.text.toString())
        limpiarCampos()
        Toast.makeText(this,"Texto enviado exitosamente",Toast.LENGTH_LONG).show()
    }

    fun mostrarTodo(){
        FirebaseFirestore.getInstance().collection("textos")
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if(firebaseFirestoreException!=null){
                    aler("No se ha podido realizar la consulta")
                    return@addSnapshotListener
                }
                var lista =ArrayList<String>()
                for (doc in querySnapshot!!){
                    var cad = doc.getString("telefono")+"\n"+doc.getString("mensaje")
                    lista.add(cad)
                }
                binding.sentList.adapter=ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,lista)
            }
    }

    fun insertarFB(tel:String,men:String){
        var datos = hashMapOf(
            "telefono" to tel,
            "mensaje" to men
        )
        FirebaseFirestore.getInstance().collection("textos")
            .add(datos)
            .addOnSuccessListener {
                println("Insertado en firebase")
            }
            .addOnFailureListener {
                aler(it.message!!)
            }
    }

    fun limpiarCampos(){
        binding.txtTel.setText("")
        binding.txtMen.setText("")
    }

    private fun toast(m:String){
        Toast.makeText(this,m,Toast.LENGTH_LONG).show()
    }
    fun aler(m:String){
        AlertDialog.Builder(this)
            .setTitle("Atención")
            .setMessage(m)
            .setPositiveButton("OK"){d,i->}
            .show()
    }

}