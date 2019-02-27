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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class MezclaNaturalEjemplo2 {

  public static class Lector implements MezclaNaturalGenerico.Lector<String> {

    private final BufferedReader br;
    private String siguienteLinea;

    public Lector(File archivo) {
      try {
        br = new BufferedReader(new FileReader(archivo));
        siguienteLinea = br.readLine();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

    @Override
    public boolean hasNext() {
      return siguienteLinea != null;
    }

    @Override
    public String next() {
      try {
        String linea = siguienteLinea;
        siguienteLinea = br.readLine();
        return linea;
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

    @Override
    public void close() throws IOException {
      br.close();
    }
  }

  public static class Escritor implements MezclaNaturalGenerico.Escritor<String> {

    private final BufferedWriter bw;

    public Escritor(File archivo) {
      try {
        bw = new BufferedWriter(new FileWriter(archivo));
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

    @Override
    public void accept(String s) {
      try {
        bw.write(s);
        bw.newLine();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

    @Override
    public void close() throws IOException {
      bw.close();
    }
  }

  public static void main(String[] args) throws Exception {
    Files.copy(
        Paths.get("nombres.txt"),
        Paths.get("nombres_copia.txt"),
        StandardCopyOption.REPLACE_EXISTING);
    MezclaNaturalGenerico<String> ordernamiento =
        new MezclaNaturalGenerico<>(Lector::new, Escritor::new);
    File entrada = new File("nombres_copia.txt");
    File temp1 = File.createTempFile("MezclaNatual", "temp");
    File temp2 = File.createTempFile("MezclaNatual", "temp");
    ordernamiento.verificarOrdenamiento(entrada);
    ordernamiento.ordenar(entrada, temp1, temp2);
    ordernamiento.verificarOrdenamiento(entrada);
    temp1.delete();
    temp2.delete();
  }
}
