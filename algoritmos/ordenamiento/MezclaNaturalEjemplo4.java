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
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Random;

public class MezclaNaturalEjemplo4 {

  public static class Persona implements Serializable, Comparable<Persona> {

    private String name;
    private int age;

    public Persona(String name, int age) {
      this.name = name;
      this.age = age;
    }

    @Override
    public String toString() {
      return age + " " + name;
    }

    private static final Comparator<Persona> COMPARADOR =
        Comparator.comparingInt((Persona p) -> p.age)
                  .thenComparing(p -> p.name);

    @Override
    public int compareTo(Persona p) {
      return COMPARADOR.compare(this, p);
    }
  }

  public static class Lector
      implements MezclaNaturalGenerico.Lector<Persona> {

    private final ObjectInputStream ois;
    private Persona siguientePersona;

    public Lector(File archivo) {
      try {
        ois = new ObjectInputStream(new FileInputStream(archivo));
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
      leerSiguientePersona();
    }

    @Override
    public boolean hasNext() {
      return siguientePersona != null;
    }

    @Override
    public Persona next() {
      Persona persona = siguientePersona;
      leerSiguientePersona();
      return persona;
    }

    // Leer por adelantado el siguiente elemento para
    // retornar el valor correcto en hasNext().
    private void leerSiguientePersona() {
      try {
        siguientePersona = (Persona) ois.readObject();
      } catch (EOFException e) {
        siguientePersona = null;
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
      implements MezclaNaturalGenerico.Escritor<Persona> {

    private final ObjectOutputStream oos;

    public Escritor(File archivo) {
      try {
        oos = new ObjectOutputStream(new FileOutputStream(archivo));
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

    @Override
    public void accept(Persona persona) {
      try {
        oos.writeObject(persona);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

    @Override
    public void close() throws IOException {
      oos.close();
    }
  }

  public static void crearArchivoBinario(String entrada, String salida) throws IOException {
    try (BufferedReader br = new BufferedReader(new FileReader(entrada));
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(salida, false))) {
      Random r = new Random();
      for (String line; (line = br.readLine()) != null; ) {
        oos.writeObject(new Persona(line, r.nextInt(100)));
      }
    }
  }

  public static void main(String[] args) throws Exception {
    crearArchivoBinario("nombres.txt", "personas.ser");
    MezclaNaturalGenerico<Persona> ordernamiento =
        new MezclaNaturalGenerico<>(Lector::new, Escritor::new);
    File entrada = new File("personas.ser");
    ordernamiento.verificarOrdenamiento(entrada);
    ordernamiento.ordenar(entrada);
    ordernamiento.desplegar(entrada);
    ordernamiento.verificarOrdenamiento(entrada);
  }
}
