#include <iostream>

#include "datos_mnist.h"
#include "etiquetas_mnist.h"

int main()
{
  DatosMnist datos("./train-images-idx3-ubyte");
  std::cout << "Num imagenes: " << datos.n_imagenes() << std::endl;
  std::cout << "Num filas: " << datos.n_filas() << std::endl;
  std::cout << "Num columnas: " << datos.n_columnas() << std::endl;
  EtiquetasMnist etiquetas("./train-labels-idx1-ubyte");
  std::cout << "Num etiquetas" << etiquetas.n_etiquetas() << std::endl;

  // Imprimir pixeles y etiqueta de la primer muestra.
  std::cout << "Etiqueta: " << int(etiquetas.etiquetas()[0]) << std::endl;
  uint8_t* pixel = (uint8_t *)datos.pixeles();
  for (int f = 0; f < datos.n_filas(); ++f) {
    for (int c = 0; c < datos.n_columnas(); ++c) {
      std::cout << ((*pixel++ > 127) ? '*' : ' ');
    }
    std::cout << std::endl;
  }
}
