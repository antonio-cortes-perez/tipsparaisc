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

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

/**
 * https://tipsparaisc.blogspot.com/2019/02/ordenamiento-mezcla-natural-cadenas.html
 */
public class MezclaNaturalEjemplo1 {

  public static class Lector implements MezclaNaturalGenerico.Lector<String> {

    private final DataInputStream dis;

    public Lector(File archivo) {
      try {
        dis = new DataInputStream(new FileInputStream(archivo));
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

    @Override
    public boolean hasNext() {
      try {
        return dis.available() != 0;
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

    @Override
    public String next() {
      try {
        return dis.readUTF();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

    @Override
    public void close() throws IOException {
      dis.close();
    }
  }

  public static class Escritor implements MezclaNaturalGenerico.Escritor<String> {

    private final DataOutputStream dos;

    public Escritor(File archivo) {
      try {
        dos = new DataOutputStream(new FileOutputStream(archivo));
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

    @Override
    public void accept(String s) {
      try {
        dos.writeUTF(s);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

    @Override
    public void close() throws IOException {
      dos.close();
    }
  }

  public static void crearArchivoBinario(String entrada, String salida) throws IOException {
    try (BufferedReader br = new BufferedReader(new FileReader(entrada));
        DataOutputStream dos = new DataOutputStream(new FileOutputStream(salida, false))) {
      for (String line; (line = br.readLine()) != null; ) {
        dos.writeUTF(line);
      }
    }
  }

  public static void main(String[] args) throws Exception {
    crearArchivoBinario("nombres.txt", "nombres.utf");
    MezclaNaturalGenerico<String> ordernamiento =
        new MezclaNaturalGenerico<>(Lector::new, Escritor::new);
    File entrada = new File("nombres.utf");
    ordernamiento.verificarOrdenamiento(entrada);
    ordernamiento.ordenar(entrada);
    ordernamiento.verificarOrdenamiento(entrada);
    ordernamiento.desplegar(entrada);
  }

}
