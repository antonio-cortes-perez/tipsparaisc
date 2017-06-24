/*
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

import java.io.*;

/**
 * @author Mariana
 * @version 31/Diciembre/2010
 */
public class MezclaNatural {
    InputStreamReader ISR = new InputStreamReader(System.in);
    BufferedReader BR = new BufferedReader(ISR);

    public void crearArchivoDatos(String nombreArchivo) throws Exception {

        String nombre = null;

        //Declaración del objeto asociado a la creación o apertura de un archivo
        DataOutputStream dos = null;

        //El siguiente código abre o crea un archivo
        try {
            dos = new DataOutputStream(new FileOutputStream(nombreArchivo, false));
        } catch (IOException e) {
            System.out.println("Error de Apertura o Creacion");
        }

        //El siguiente bloque escribe un registro en el archivo abierto o creado
        try {
            do {
                System.out.println("Nombre: [Solo presiona Enter para terminar de capturar nombres]");
                nombre = BR.readLine();
                if (!nombre.equalsIgnoreCase("")) {
                    dos.writeUTF(nombre);
                }

            } while (!nombre.equalsIgnoreCase(""));
        } catch (IOException e) {
            System.out.println("Error de escritura");
        } finally {
            dos.close();
        }
    }

    //Metodo para desplegar el contenido del archivo que se creó o se abrió en las líneas anteriores
    public void desplegar(String nombreArchivo) throws Exception {
        String nombre = null;

        DataInputStream dis = null;
        int index = 0;
        //DataOutputStream dos = null;
        try {
            dis = new DataInputStream(new FileInputStream(nombreArchivo));
            while (dis.available() != 0) {
                nombre = dis.readUTF();
                System.out.println(++index + ") " + nombre);
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error de Apertura-Lectura archivo: " + nombreArchivo);
        } catch (IOException e) {
            System.out.println("Error de lectura archivo: " + nombreArchivo);
        } finally {
            if (dis != null) {
                dis.close();
            }
        }
    }

    //Metodo para verificar el correcto orden en el archivo
    public void verificarOrdenamiento(String nombreArchivo) throws IOException {
        String actual = null;
        String anterior = null;

        //Variable booleana para indicar el estado del archivo
        boolean estaOrdenado = true;

        DataInputStream dis = null;
        //DataOutputStream dos = null;
        try {
            dis = new DataInputStream(new FileInputStream(nombreArchivo));

            //Ciclo para verificar el orden del archivo
            //Comenzar siempre por averiguar si hay datos dentro del archivo
            while (dis.available() != 0) {
                //En un primer momento los indices quedan a la par
                anterior = actual;
                //actual se encargara de ir "jalando" a anterior
                actual = dis.readUTF();

                //En la segunda vuelta, el indice anterior ocupa la posicion
                //del indice actual y a partir de aqui, el indice actual
                //se despega del anterior
                if (anterior == null) {
                    anterior = actual;
                }

                System.out.println(actual);

                //Comparacion de los datos contenidos en actual y anterior
                //Condicion: Si el dato anterior es lexicograficamente mayor al actual
                if (anterior.compareTo(actual) > 0) {
                    System.out.println("Error en el ordenamiento");
                    //Actualizacion de la variable booleana que indica el estado del archivo
                    estaOrdenado = false;
                    //Interrupcion del ciclo
                    break;
                }
            }

            //Si la variable booleana conservo su valor original de true, desplegar un mensaje
            if (estaOrdenado) {
                System.out.println("EL ARCHIVO ESTA ORDENADO");
            }

        } catch (FileNotFoundException e) {
            System.out.println("Error de Apertura-Lectura archivo: " + nombreArchivo);
        } catch (IOException e) {
            System.out.println("Error de lectura archivo: " + nombreArchivo);
        } finally {
            //Verificar siempre que el archivo este abierto antes de intentar cerrarlo
            if (dis != null) {
                dis.close();
            }

        }
    }

    //Metodo para generar particiones de secuencias
    public boolean particion(String nombreArchivo, String archivo1, String archivo2) {

        //Se utilizara una logica similar a la del metodo de verificar orden
        //por lo que los indices son declarados de la misma manera
        String actual = null;
        String anterior = null;

        //Variable para controlar el indice del archivo al cual se va a escribir.
        //El archivo en cuestion es declarado dentro de un arreglo de archivos
        int indexOutputStream = 0;

        //Variable que determina si existe un cambio de secuencia en el ordenamiento
        boolean hayCambioDeSecuencia = false;

        //Declaracion de los objetos asociados a los archivos y del arreglo de archivos
        //que sirven para las particiones
        DataOutputStream dos[] = new DataOutputStream[2];
        DataInputStream dis = null;

        try {
            //Abre o crea los archivos
            dos[0] = new DataOutputStream(new FileOutputStream(archivo1, false));
            dos[1] = new DataOutputStream(new FileOutputStream(archivo2, false));
            dis = new DataInputStream(new FileInputStream(nombreArchivo));

            //Primero, verifica si existen datos en el archivo que se va a leer
            while (dis.available() != 0) {
                //Utiliza la misma logica para las variables que almacenan los datos
                //que en el metodo de la verificacion del orden
                anterior = actual;
                actual = dis.readUTF();

                if (anterior == null) {
                    anterior = actual;
                }

                //Cambio de secuencia. Manipulacion del indice del arreglo de archivos
                if (anterior.compareTo(actual) > 0) {
                    indexOutputStream = indexOutputStream == 0 ? 1 : 0;
                    //Actualizacion de la variable booleana, esto indica la existencia
                    //de un cambio de secuencia
                    hayCambioDeSecuencia = true;
                }

                //Imprimir el dato contenido en actual y escribirlo en el archivo correspondiente
                //System.out.println(indexOutputStream + ") "+ actual);
                dos[indexOutputStream].writeUTF(actual);
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error lectura/escritura");
        } catch (IOException e) {
            System.out.println("Error en la creacion o apertura del archivo 1");
        } finally {
            //Verificar para cada archivo, que efectivamente se encuentre abierto
            //antes de cerrarlo
            try {
                if (dis != null) {
                    dis.close();
                }

                if (dos[0] != null) {
                    dos[0].close();
                }

                if (dos[1] != null) {
                    dos[1].close();
                }
            } catch (IOException ex) {
                System.out.println("Error al cerrar archivos");
            }
        }
        //El valor retornado sirve para determinar cuando existe una particion
        return hayCambioDeSecuencia;
    }

    //Metodo de fusion de los datos obtenidos en el metodo de particion
    public void fusion(String nombreArchivo, String archivo1, String archivo2) {
        //Variables para almacenar los datos de los archivos
        //que contienen las particiones
        String[] actual = new String[2];
        String[] anterior = new String[2];
        boolean[] finArchivo = new boolean[2];
        int indexArchivo = 0;

        //Creacion de los objetos asociacos a los archivos
        DataOutputStream dos = null;
        DataInputStream dis[] = new DataInputStream[2];

        try {
            //Abre o crea los archivos
            dis[0] = new DataInputStream(new FileInputStream(archivo1));
            dis[1] = new DataInputStream(new FileInputStream(archivo2));
            dos = new DataOutputStream(new FileOutputStream(nombreArchivo, false));

            //Condicion principal: debe haber datos en ambos archivos de lectura
            //Es importante notar que al inicio siempre hay al menos un dato en
            //cada archivo, de otra forma el metodo de particion hubiera
            //generado una sola secuencia y no entrariamos a la fusion.
            while (dis[0].available() != 0 && dis[1].available() != 0) {

                // 1era vez: inicializar con la primera palabra de cada archivo
                if (anterior[0] == null && anterior[1] == null) {
                    anterior[0] = actual[0] = dis[0].readUTF();
                    anterior[1] = actual[1] = dis[1].readUTF();
                }

                // al inicio del procesamiento de dos secuencias, anterior y
                // actual apuntan a la primer palabra de cada secuencia.
                anterior[0] = actual[0];
                anterior[1] = actual[1];

                // mezclamos las dos secuencias hasta que una acaba
                while (anterior[0].compareTo(actual[0]) <= 0 &&
                        anterior[1].compareTo(actual[1]) <= 0) {
                    indexArchivo = (actual[0].compareTo(actual[1]) <= 0) ? 0 : 1;
                    dos.writeUTF(actual[indexArchivo]);
                    anterior[indexArchivo] = actual[indexArchivo];

                    // salir del while cuando no haya datos, pero ya procesamos
                    // el ultimo nombre del archivo
                    if (dis[indexArchivo].available() != 0) {
                        actual[indexArchivo] = dis[indexArchivo].readUTF();
                    } else {
                        finArchivo[indexArchivo] = true;
                        break;
                    }
                }

                // en este punto indexArchivo nos dice que archivo causo
                // que salieramos del while anterior, por lo que tenemos
                // que purgar el otro archivo
                indexArchivo = indexArchivo == 0 ? 1 : 0;

                while (anterior[indexArchivo].compareTo(actual[indexArchivo]) <= 0) {
                    dos.writeUTF(actual[indexArchivo]);
                    anterior[indexArchivo] = actual[indexArchivo];
                    if (dis[indexArchivo].available() != 0) {
                        actual[indexArchivo] = dis[indexArchivo].readUTF();
                    } else {
                        finArchivo[indexArchivo] = true;
                        break;
                    }
                }
            }

            // purgar los dos archivos en caso de que alguna secuencia
            // haya quedado sola al final del archivo.
            // Para salir del while anterior alguno de los 2 archivos
            // debio terminar, por lo que a lo mas uno de los dos whiles
            // siguientes se ejecutara
            if (!finArchivo[0]) {
                dos.writeUTF(actual[0]);
                while (dis[0].available() != 0) {
                    dos.writeUTF(dis[0].readUTF());
                }
            }

            if (!finArchivo[1]) {
                dos.writeUTF(actual[1]);
                while (dis[1].available() != 0) {
                    dos.writeUTF(dis[1].readUTF());
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println(e);
        } catch (IOException e) {
            System.err.println(e);
        } finally {
            //Verificar para cada archivo, que efectivamente se encuentre abierto
            //antes de cerrarlo
            try {
                if (dis[0] != null) {
                    dis[0].close();
                }

                if (dis[1] != null) {
                    dis[1].close();
                }

                if (dos != null) {
                    dos.close();
                }
            } catch (IOException ex) {
                System.out.println("Error al cerrar archivos");
            }
        }
    }

    public void ordenar(String nombreArchivo) {
        /*No es recomendable utilizar nombres fijos para los archivos,
         *pues se podria sobreescribir accidentalmente otro archivo con
         *el mismo nombre, sin embargo, para fines de este proyecto
         *se utilizaron nombres fijos
         */
        int index = 0;
        while (particion(nombreArchivo, "archivo1.txt", "archivo2.txt")) {
            //Imprime el numero de particiones-fusiones que le llevo a los
            //metodos de particion y fusion el ordenar el archivo
            System.out.println("Fusion " + ++index);
            fusion(nombreArchivo, "archivo1.txt", "archivo2.txt");
        }
    }

    public static void main(String [] args)throws Exception{
        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(isr);

        String nombreArchivo = null;

        MezclaNatural mezcla1 = new MezclaNatural();

        //Solicita el nombre de un archivo para poder ordenarlo
        System.out.println("Nombre del archivo:");
        nombreArchivo = br.readLine();

        //Despliega el contenido del archivo sin ordenar
        mezcla1.desplegar(nombreArchivo);

        //Ordena el contenido del archivo
        mezcla1.ordenar(nombreArchivo);

        //Verifica que el archivo este ordenado correctamente
        mezcla1.verificarOrdenamiento(nombreArchivo);

    }
}
