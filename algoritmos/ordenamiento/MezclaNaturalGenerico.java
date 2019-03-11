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

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * https://tipsparaisc.blogspot.com/2019/02/ordenamiento-mezcla-natural-generico.html
 */
public class MezclaNaturalGenerico<T extends Comparable<T>> {
  public interface Lector<T> extends Iterator<T>, Closeable { }
  public interface Escritor<T> extends Consumer<T>, Closeable { }

  private final Function<File, Lector<T>> generaLector;
  private final Function<File, Escritor<T>> generaEscritor;

  // Metodo para desplegar el contenido.
  public void desplegar(File entrada) throws Exception {
    int index = 0;
    try(Lector<T> dis = generaLector.apply(entrada)) {
      while (dis.hasNext()) {
        System.out.println(++index + ") " + dis.next());
      }
    }
  }

  // Metodo para verificar el ordenamiento.
  public void verificarOrdenamiento(File entrada) throws IOException {
    T actual = null;
    T anterior = null;

    //Variable booleana para indicar el estado del archivo
    boolean estaOrdenado = true;

    try(Lector<T> dis = generaLector.apply(entrada)) {
      //Ciclo para verificar el orden del archivo
      //Comenzar siempre por averiguar si hay datos dentro del archivo
      while (dis.hasNext()) {
        //En un primer momento los indices quedan a la par
        anterior = actual;
        //actual se encargara de ir "jalando" a anterior
        actual = dis.next();

        //En la segunda vuelta, el indice anterior ocupa la posicion
        //del indice actual y a partir de aqui, el indice actual
        //se despega del anterior
        if (anterior == null) {
          anterior = actual;
        }

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

    }
  }

  //Metodo para generar particiones de secuencias
  private boolean particion(File entrada, File temp1, File temp2) {

    //Se utilizara una logica similar a la del metodo de verificar orden
    //por lo que los indices son declarados de la misma manera
    T actual = null;
    T anterior = null;

    //Variable para controlar el indice del archivo al cual se va a escribir.
    //El archivo en cuestion es declarado dentro de un arreglo de archivos
    int indexOutputStream = 0;

    //Variable que determina si existe un cambio de secuencia en el ordenamiento
    boolean hayCambioDeSecuencia = false;

    //Declaracion de los objetos asociados a los archivos y del arreglo de archivos
    //que sirven para las particiones
    @SuppressWarnings("unchecked")
    Escritor<T> dos[] = new Escritor[2];
    Lector<T> dis = null;

    try {
      //Abre o crea los archivos
      dos[0] = generaEscritor.apply(temp1);
      dos[1] = generaEscritor.apply(temp2);
      dis = generaLector.apply(entrada);

      //Primero, verifica si existen datos en el archivo que se va a leer
      while (dis.hasNext()) {
        //Utiliza la misma logica para las variables que almacenan los datos
        //que en el metodo de la verificacion del orden
        anterior = actual;
        actual = dis.next();

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
        dos[indexOutputStream].accept(actual);
      }
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
  private void fusion(File salida, File temp1, File temp2) {
    //Variables para almacenar los datos de los archivos
    //que contienen las particiones
    List<T> actual = Arrays.asList(null, null);
    List<T> anterior = Arrays.asList(null, null);
    boolean[] finArchivo = new boolean[2];
    int indexArchivo = 0;

    //Creacion de los objetos asociacos a los archivos
    Escritor<T> dos = null;
    @SuppressWarnings("unchecked")
    Lector<T> dis[] = new Lector[2];

    try {
      //Abre o crea los archivos
      dis[0] = generaLector.apply(temp1);
      dis[1] = generaLector.apply(temp2);
      dos = generaEscritor.apply(salida);

      //Condicion principal: debe haber datos en ambos archivos de lectura
      //Es importante notar que al inicio siempre hay al menos un dato en
      //cada archivo, de otra forma el metodo de particion hubiera
      //generado una sola secuencia y no entrariamos a la fusion.
      while (dis[0].hasNext() && dis[1].hasNext()) {

        // 1era vez: inicializar con la primera palabra de cada archivo
        if (anterior.get(0) == null && anterior.get(1) == null) {
          actual.set(0, dis[0].next());
          anterior.set(0, actual.get(0));
          actual.set(1, dis[1].next());
          anterior.set(1, actual.get(1));
        }

        // al inicio del procesamiento de dos secuencias, anterior y
        // actual apuntan a la primer palabra de cada secuencia.
        anterior.set(0, actual.get(0));
        anterior.set(1, actual.get(1));

        // mezclamos las dos secuencias hasta que una acaba
        while (anterior.get(0).compareTo(actual.get(0)) <= 0 &&
            anterior.get(1).compareTo(actual.get(1)) <= 0) {
          indexArchivo = (actual.get(0).compareTo(actual.get(1)) <= 0) ? 0 : 1;
          dos.accept(actual.get(indexArchivo));
          anterior.set(indexArchivo, actual.get(indexArchivo));

          // salir del while cuando no haya datos, pero ya procesamos
          // el ultimo nombre del archivo
          if (dis[indexArchivo].hasNext()) {
            actual.set(indexArchivo, dis[indexArchivo].next());
          } else {
            finArchivo[indexArchivo] = true;
            break;
          }
        }

        // en este punto indexArchivo nos dice que archivo causo
        // que salieramos del while anterior, por lo que tenemos
        // que purgar el otro archivo
        indexArchivo = indexArchivo == 0 ? 1 : 0;

        while (anterior.get(indexArchivo).compareTo(actual.get(indexArchivo)) <= 0) {
          dos.accept(actual.get(indexArchivo));
          anterior.set(indexArchivo, actual.get(indexArchivo));
          if (dis[indexArchivo].hasNext()) {
            actual.set(indexArchivo, dis[indexArchivo].next());
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
        dos.accept(actual.get(0));
        while (dis[0].hasNext()) {
          dos.accept(dis[0].next());
        }
      }

      if (!finArchivo[1]) {
        dos.accept(actual.get(1));
        while (dis[1].hasNext()) {
          dos.accept(dis[1].next());
        }
      }
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

  /** Ordena entrada usando temp1 and temp2 como almacenamiento auxiliar. */
  public void ordenar(File entrada) throws IOException {
    int index = 0;
    File temp1 = null, temp2 = null;
    try {
      temp1 = File.createTempFile("MezclaNatual", "temp");
      temp2 = File.createTempFile("MezclaNatual", "temp");
      while (particion(entrada, temp1, temp2)) {
        // Imprime el numero de particiones-fusiones que le llevo a los
        // metodos de particion y fusion el ordenar el archivo
        System.out.println("Fusion " + ++index);
        fusion(entrada, temp1, temp2);
      }
    } finally {
      if (temp1 != null) temp1.delete();
      if (temp2 != null) temp2.delete();
    }
  }

  public MezclaNaturalGenerico(
      Function<File, Lector<T>> generaLector,
      Function<File, Escritor<T>> generaEscritor) {
    this.generaLector = generaLector;
    this.generaEscritor = generaEscritor;
  }
}
