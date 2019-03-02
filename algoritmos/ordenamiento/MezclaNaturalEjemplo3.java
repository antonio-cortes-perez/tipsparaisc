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

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.util.Random;

public class MezclaNaturalEjemplo3 {

  public static class Lector
      implements MezclaNaturalGenerico.Lector<BigInteger> {

    private final ObjectInputStream ois;
    private BigInteger siguienteEntero;

    public Lector(File archivo) {
      try {
        ois = new ObjectInputStream(new FileInputStream(archivo));
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
      leerSiguienteEntero();
    }

    @Override
    public boolean hasNext() {
      return siguienteEntero != null;
    }

    @Override
    public BigInteger next() {
      BigInteger entero = siguienteEntero;
      leerSiguienteEntero();
      return entero;
    }

    // Leer por adelantado el siguiente elemento para
    // retornar el valor correcto en hasNext().
    private void leerSiguienteEntero() {
      try {
        siguienteEntero = (BigInteger) ois.readObject();
      } catch (EOFException e) {
        siguienteEntero = null;
      } catch (IOException | ClassNotFoundException e) {
        throw new RuntimeException(e);
      }
    }

    @Override
    public void close() throws IOException {
      ois.close();
    }
  }

  public static class Escritor
      implements MezclaNaturalGenerico.Escritor<BigInteger> {

    private final ObjectOutputStream oos;

    public Escritor(File archivo) {
      try {
        oos = new ObjectOutputStream(new FileOutputStream(archivo));
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

    @Override
    public void accept(BigInteger s) {
      try {
        oos.writeObject(s);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

    @Override
    public void close() throws IOException {
      oos.close();
    }
  }

  public static void crearArchivoBinario(String archivo) throws IOException {
    try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(archivo, false))) {
      int num_bits = 128;
      Random r = new Random();
      for (int i = 0; i < 100000; ++i) {
        oos.writeObject(new BigInteger(num_bits, r));
      }
    }
  }

  public static void main(String[] args) throws Exception {
    crearArchivoBinario("enteros_grandes.ser");
    MezclaNaturalGenerico<BigInteger> ordernamiento =
        new MezclaNaturalGenerico<>(Lector::new, Escritor::new);
    File entrada = new File("enteros_grandes.ser");
    ordernamiento.verificarOrdenamiento(entrada);
    ordernamiento.ordenar(entrada);
    ordernamiento.desplegar(entrada);
    ordernamiento.verificarOrdenamiento(entrada);
  }

}
