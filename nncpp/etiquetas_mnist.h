#ifndef ETIQUETAS_MNIST_
#define ETIQUETAS_MNIST_

#include <cstdint>
#include <fstream>

// Logica para cargar archivo de etiquetas MNIST
// Mas detalles acerca del formato:
// http://yann.lecun.com/exdb/mnist/
class EtiquetasMnist
{
public:
  EtiquetasMnist(const std::string& nombre_archivo) {
    leer_etiquetas_mnist(nombre_archivo);
  }

  uint32_t n_etiquetas()
  {
    return union_etiquetas_.datos.n_etiquetas;
  }

  char* etiquetas()
  {
    return union_etiquetas_.datos.etiquetas;
  }

private:
  // Estructura de un archivo de etiquetas:
  // - 4 bytes: numero magico
  // - 4 bytes: numero de etiquetas
  // - num etiquetas bytes: etiquetas
  static constexpr int TAMANO_ENCABEZADO_ETIQUETAS_MNIST = 8;

  struct StructEtiquetasMnist
  {
    uint32_t n_magico;
    uint32_t n_etiquetas;
    char* etiquetas;
  };

// Facilita leer datos del archivo.
  union UnionEtiquetasMnist
  {
    char arreglo[TAMANO_ENCABEZADO_ETIQUETAS_MNIST];
    StructEtiquetasMnist datos;
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
  void leer_etiquetas_mnist(const std::string& nombre_archivo)
  {
    std::ifstream archivo;
    archivo.open(nombre_archivo, std::ios::in | std::ios::binary);
    archivo.read(union_etiquetas_.arreglo, TAMANO_ENCABEZADO_ETIQUETAS_MNIST);
    // Checar si es necesario transformar de big-endian to little-endian.
    if (union_etiquetas_.datos.n_magico != 2049)
    {
      for (int i = 0; i < 2; ++i)
      {
        invertir_bytes(union_etiquetas_.arreglo + (i * 4),
                       union_etiquetas_.arreglo + (i * 4) + 3);
      }
    }
    int n_etiquetas = union_etiquetas_.datos.n_etiquetas;
    union_etiquetas_.datos.etiquetas = new char[n_etiquetas];
    archivo.read(union_etiquetas_.datos.etiquetas, n_etiquetas);
    archivo.close();
  }

  UnionEtiquetasMnist union_etiquetas_;
};

#endif  // ETIQUETAS_MNIST_
