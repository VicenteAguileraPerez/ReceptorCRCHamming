package com.vicenteaguilera.receptorcrchamming;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
{
    String dato,divisior,tramaHammingCrc;
    boolean par_o_impar;
    RadioGroup radioGroup;
    EditText editText_entrada,editText_divisor,editText_salida;
    RadioButton radioButton_par,radioButton_impar;
    Button button_generar,button_limpiar;
    TextView textView_error;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.e("ñ",""+((char)(165)));
        Log.e("Ñ",""+((char)(209)));
        setContentView(R.layout.activity_main);
        editText_entrada = findViewById(R.id.editText_entrada);
        editText_divisor = findViewById(R.id.editText_divisor);
        editText_salida = findViewById(R.id.editText_trama);
        radioGroup = findViewById(R.id.radio_group);
        radioButton_par = findViewById(R.id.radioButton_par);
        radioButton_impar = findViewById(R.id.radioButton_impar);
        button_generar = findViewById(R.id.button_generar);
        button_limpiar = findViewById(R.id.button_limpiar);
        textView_error = findViewById(R.id.textView_error);

        button_limpiar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                editText_entrada.setText("");
                editText_divisor.setText("");
                editText_salida.setText("");
                radioGroup.clearCheck();
                textView_error.setText("");

            }
        });
        button_generar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                textView_error.setText("");
                dato = editText_entrada.getText().toString();
                divisior= editText_divisor.getText().toString();
                if(isNumeric(dato) && dato.length()>=12)
                {
                    if(isBinario(Long.parseLong(dato)))
                    {

                        if (isNumeric(editText_divisor.getText().toString()))
                        {
                            if (isBinario(Integer.parseInt(divisior)) && Integer.parseInt(divisior)!=0)
                            {

                                if (radioButton_par.isChecked())
                                {
                                    par_o_impar=true;
                                    aplicarHamming(CRC(dato,true),dato);
                                }
                                else if (radioButton_impar.isChecked())
                                {
                                    par_o_impar=false;
                                    aplicarHamming(CRC(dato,true),dato);
                                }
                                else
                                {
                                    Toast.makeText(MainActivity.this, "Seleccione la paridad", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else
                            {
                                Toast.makeText(MainActivity.this, "No es un binario el divisor o no es diferente de 0", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "Debe de introducir divisor valido ejemplo 1010", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        Toast.makeText(MainActivity.this, "La trama no está en binario", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(MainActivity.this,"Debe introducir la trama con 0's y 1's ejemplo 10001001000100o bien, la trama no tiene 14 valores",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private boolean isBinario(long valor)
    {
        boolean bandera = true;
        for(char e : String.valueOf(valor).toCharArray())
        {
            if(e!='1' && e!='0')
            {
                bandera = false;
                break;
            }
        }
        return bandera;
    }
    private int convertToDecimal(String binario)
    {

        int dato=0;
        for (int i = 0; i < binario.length(); i++) {
            if (binario.charAt(i) == '1') {
                dato += Math.pow(2, binario.length() - i - 1);
            } else {
                dato += 0;
            }


        }
        return dato;

    }
    private boolean isNumeric(String valor)
    {
        try {
            Long.parseLong(dato);
            return true;
        }
        catch (NumberFormatException nfe)
        {
            return false;
        }

    }
    private boolean CRC(String tramaFinal,boolean segunda)
    {
        String bitCRC=obtenerBitCrc(divisior.length(),tramaFinal);
        tramaHammingCrc=eliminarCerosIzquierda(tramaFinal);

        String res_xor="";
        String aux = tramaHammingCrc.substring(0,divisior.length());
        Log.e("b",tramaHammingCrc.length()+" "+tramaHammingCrc+" "+aux);
        int i = aux.length();
        do {
            for (int j = 0; j < divisior.length(); j++) {
                res_xor += xOr(String.valueOf(aux.charAt(j)), String.valueOf(divisior.charAt(j)));
            }
            res_xor = eliminarCerosIzquierda(res_xor);
            while (res_xor.length() < divisior.length() && i!=tramaHammingCrc.length())
            {
                res_xor += String.valueOf(tramaHammingCrc.charAt(i));
                res_xor = eliminarCerosIzquierda(res_xor);
                i++;
            }
            aux = res_xor;
            res_xor = "";
        }while (i<tramaHammingCrc.length());

        if(aux.equals(divisior) || aux.equals(""))
        {
            String bits = bitsValidos(tramaFinal);
            Log.e("P",bits+tramaHammingCrc.length());
            editText_salida.setText(""+(char)convertToDecimal(bits));
            if(segunda) {
                textView_error.setText("Dio con el CRC, no se aplicó hamming");
            }
            return true;
        }
        else
        {
            //aplicar hamming
            return false;
        }

    }
    private String Hamming(String binario) {
        String hammingTrama = binario.substring(0, 12);

        /**
         * Coloca los datos en donde no hay potencias de 2 en el vector de hamming
         */
        //0 0 0 0 0 0 0 0 0 0 0 0
        //1 2 3 4 5 6 7 8 9 10 11 12

        String comprobacion = "";
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                if (i == 0 && j == 0) {
                    //vector de 5
                    String paridades = ""+ hammingTrama.charAt(0)+ hammingTrama.charAt(2) + hammingTrama.charAt(4) + hammingTrama.charAt(6) + hammingTrama.charAt(8) + hammingTrama.charAt(10);

                    comprobacion = String.valueOf(Paridades(paridades))+comprobacion;
                    Log.d("b", "vector 1");
                    Log.d("b", comprobacion);

                } else if (i == 0 && j == 1) {
                    //vector de 5
                    String paridades = ""+hammingTrama.charAt(1)+  hammingTrama.charAt(2) + hammingTrama.substring(5, 7) + hammingTrama.substring(9, 11);
                    comprobacion = String.valueOf(Paridades(paridades))+comprobacion;
                    Log.d("b", "vector 2");
                    Log.d("b", comprobacion);
                } else if (i == 1 && j == 0) {
                    //vector de 4
                    String paridades = ""+hammingTrama.charAt(3) + hammingTrama.charAt(4) + hammingTrama.charAt(5) + hammingTrama.charAt(6) + hammingTrama.charAt(11);
                    comprobacion = String.valueOf(Paridades(paridades))+comprobacion;
                    Log.d("b", "vector 3");
                    Log.d("b", comprobacion);
                } else {
                    //vector de 4
                    String paridades = hammingTrama.substring(7, 12);
                    comprobacion = String.valueOf(Paridades(paridades))+comprobacion;
                    Log.d("b", "vector 4");
                    Log.d("b", comprobacion);
                }
            }

        }
        if(Integer.parseInt(comprobacion)==0)
        {

            return binario;
        }
        else
        {
            int posError= convertToDecimal("0000"+comprobacion)-1;

            char binarioA[];
            if(binario.charAt(posError)=='0')
            {
                binarioA=binario.toCharArray();
                binarioA[posError]='1';
            }
            else
            {
                binarioA=binario.toCharArray();
                binarioA[posError]='0';
            }
            comprobacion=String.valueOf(binarioA);
            Log.e("P",comprobacion);
            return comprobacion;
        }
    }
    private int Paridades(String vec)
    {
        int cant_unos=0;
        int ret0_1;
        for(int i=0;i<vec.length();i++)
        {
            if(vec.charAt(i)=='1')
            {
                cant_unos++;
            }
        }
        Log.e("b",vec+"unos="+cant_unos);
        if(par_o_impar)
        {
            ret0_1=(cant_unos%2==0)?0:1;
        }
        else
        {
            ret0_1=(cant_unos%2==0)?1:0;
        }
        return  ret0_1;
    }

    private String bitsValidos(String tramaHammingCrc)
    {
        String bitsValidos="";
        for (int i=0;i<tramaHammingCrc.length();i++)
        {
            int x =i+1;
            if(((x & (x - 1)) != 0) && bitsValidos.length()<8)//es potencia de 2
            {
                bitsValidos+=tramaHammingCrc.charAt(i);
            }
        }
        Log.e("Pau",""+bitsValidos);
        return bitsValidos;
    }
    private String xOr(String a, String b)
    {
        return a.equals(b)?"0":"1";
    }

    private String obtenerBitCrc(int divisiorLenght,String hamming)
    {
        String bitCrc="";
        int posFinal = hamming.length()-1;
        for(int i=divisiorLenght-1;i>=divisiorLenght-2;i--)
        {
            bitCrc=hamming.charAt(posFinal--)+bitCrc;
        }
        return bitCrc;
    }
    private String eliminarCerosIzquierda(String cadena)
    {
        boolean bandera_elimina_ceros_izquierda=false;
        String datos="";

        for (int i=0;i<cadena.length();i++)
        {
            if (cadena.charAt(i) =='0' && !bandera_elimina_ceros_izquierda)
            {
                continue;
            }
            else {
                bandera_elimina_ceros_izquierda = true;
                datos += cadena.charAt(i);
            }
        }
        return datos;
    }
    private void aplicarHamming(boolean tramaOK,String dato)
    {
        if(!tramaOK)
        {
            String binario =Hamming(dato);
            tramaOK=CRC(binario,false);
            if(!tramaOK)
            {
                editText_salida.setText("Los datos tienen más de un error");
            }
            else
            {
                textView_error.setText("La trama corregida es: "+binario);
            }
        }
    }
}

