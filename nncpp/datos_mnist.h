#ifndef DATOS_MNIST_
#define DATOS_MNIST_

#include <cstdint>
#include <fstream>

// Logica para cargar archivo de datos MNIST
// Mas detalles acerca del formato:
// http://yann.lecun.com/exdb/mnist/
class DatosMnist
{
public:
  DatosMnist(const std::string& nombre_archivo) {
    leer_datos_mnist(nombre_archivo);
  }

  uint32_t n_imagenes()
  {
    return union_datos_.datos.n_imagenes;
  }

  uint32_t n_filas()
  {
    return union_datos_.datos.n_filas;
  }

  uint32_t n_columnas()
  {
    return union_datos_.datos.n_columnas;
  }

  char* pixeles()
  {
    return union_datos_.datos.pixeles;
  }

private:
  // Estructura de un archivo de datos:
  // - 4 bytes: numero magico
  // - 4 bytes: numero de imagenes
  // - 4 bytes: numero de filas
  // - 4 bytes: numero de columnas
  // - num imagenes * num filas * num columnas bytes: pixeles
  static constexpr int TAMANO_ENCABEZADO_DATOS_MNIST = 16;

  struct StructDatosMnist
  {
    uint32_t n_magico;
    uint32_t n_imagenes;
    uint32_t n_filas;
    uint32_t n_columnas;
    char* pixeles;
  };

// Facilita leer datos del archivo.
  union UnionDatosMnist
  {
    char arreglo[TAMANO_ENCABEZADO_DATOS_MNIST];
    StructDatosMnist datos;
  };

// Se usa para convertir de big-endian to little-endian.
  void invertir_bytes(char* inicio, char* fin)
  {
    char temp;
    while(inicio < fin)
    {
      temp = *inicio;
      *inicio = *fin;
      *fin = temp;
      inicio++;
      fin--;
    }
  }

// Carga los datos del archivo en union_datos_.
  void leer_datos_mnist(const std::string& nombre_archivo)
  {
    std::ifstream archivo;
    archivo.open(nombre_archivo, std::ios::in | std::ios::binary);
    archivo.read(union_datos_.arreglo, TAMANO_ENCABEZADO_DATOS_MNIST);
    // Checar si es necesario transformar de big-endian to little-endian.
    if (union_datos_.datos.n_magico != 2051)
    {
      for (int i = 0; i < 4; ++i)
      {
        invertir_bytes(union_datos_.arreglo + (i * 4),
                       union_datos_.arreglo + (i * 4) + 3);
      }
    }
    int n_pixeles = union_datos_.datos.n_imagenes *
                    union_datos_.datos.n_filas * union_datos_.datos.n_columnas;
    union_datos_.datos.pixeles = new char[n_pixeles];
    archivo.read(union_datos_.datos.pixeles, n_pixeles);
    archivo.close();
  }

  UnionDatosMnist union_datos_;
};

#endif  // DATOS_MNIST_
