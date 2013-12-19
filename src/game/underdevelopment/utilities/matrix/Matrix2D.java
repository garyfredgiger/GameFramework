package game.underdevelopment.utilities.matrix;

import java.util.Vector;

import game.framework.primitives.Vector2D;

public class Matrix2D
{
  Matrix3By3 matrix;

  Matrix2D()
  {
    matrix = new Matrix3By3();

    // Initialize the matrix to an identity matrix
    identity();
  }

  // Create an identity matrix
  public void identity()
  {
    matrix.element11 = 1;
    matrix.element12 = 0;
    matrix.element13 = 0;
    matrix.element21 = 0;
    matrix.element22 = 1;
    matrix.element23 = 0;
    matrix.element31 = 0;
    matrix.element32 = 0;
    matrix.element33 = 1;
  }

  // Multiply two matrices together
  public void MatrixMultiply(Matrix3By3 m)
  {
    Matrix3By3 matrixTemp = new Matrix3By3();

    // First row
    matrixTemp.element11 = (matrix.element11 * m.element11) + (matrix.element12 * m.element21) + (matrix.element13 * m.element31);
    matrixTemp.element12 = (matrix.element11 * m.element12) + (matrix.element12 * m.element22) + (matrix.element13 * m.element32);
    matrixTemp.element13 = (matrix.element11 * m.element13) + (matrix.element12 * m.element23) + (matrix.element13 * m.element33);

    // Second
    matrixTemp.element21 = (matrix.element21 * m.element11) + (matrix.element22 * m.element21) + (matrix.element23 * m.element31);
    matrixTemp.element22 = (matrix.element21 * m.element12) + (matrix.element22 * m.element22) + (matrix.element23 * m.element32);
    matrixTemp.element23 = (matrix.element21 * m.element13) + (matrix.element22 * m.element23) + (matrix.element23 * m.element33);

    // Third
    matrixTemp.element31 = (matrix.element31 * m.element11) + (matrix.element32 * m.element21) + (matrix.element33 * m.element31);
    matrixTemp.element32 = (matrix.element31 * m.element12) + (matrix.element32 * m.element22) + (matrix.element33 * m.element32);
    matrixTemp.element33 = (matrix.element31 * m.element13) + (matrix.element32 * m.element23) + (matrix.element33 * m.element33);

    matrix = matrixTemp;
  }

  //// Applies a 2D transformation matrix to a std::vector of Vector2Ds
  //public void TransformVector2Ds(Vector<Vector2D> vPoint)
  //{
  //  for (int i=0; i<vPoint.size(); ++i)
  //  {
  //    double tempX =(m_Matrix._11*vPoint[i].x) + (m_Matrix._21*vPoint[i].y) + (m_Matrix._31);
  //
  //    double tempY = (m_Matrix._12*vPoint[i].x) + (m_Matrix._22*vPoint[i].y) + (m_Matrix._32);
  //  
  //    vPoint[i].x = tempX;
  //
  //    vPoint[i].y = tempY;
  //
  //  }
  //}

  // Applies a 2D transformation matrix to a single Vector2D
  public void TransformVector2Ds(Vector2D vPoint)
  {
    double tempX = (matrix.element11 * vPoint.x) + (matrix.element21 * vPoint.y) + (matrix.element31);
    double tempY = (matrix.element12 * vPoint.x) + (matrix.element22 * vPoint.y) + (matrix.element32);

    vPoint.x = tempX;
    vPoint.y = tempY;
  }

  // Create a transformation matrix
  public void Translate(double x, double y)
  {
    Matrix3By3 translateMatrix = new Matrix3By3();

    matrix.element11 = 1;
    matrix.element12 = 0;
    matrix.element13 = 0;
    matrix.element21 = 0;
    matrix.element22 = 1;
    matrix.element23 = 0;
    matrix.element31 = x;
    matrix.element32 = y;
    matrix.element33 = 1;

    //and multiply
    MatrixMultiply(translateMatrix);
  }

  // Create a scale matrix
  public void Scale(double xScale, double yScale)
  {
    Matrix3By3 mat = new Matrix3By3();

    mat.element11 = xScale;
    mat.element12 = 0;
    mat.element13 = 0;
    mat.element21 = 0;
    mat.element22 = yScale;
    mat.element23 = 0;
    mat.element31 = 0;
    mat.element32 = 0;
    mat.element33 = 1;

    // And multiply
    MatrixMultiply(mat);
  }

  // Create a rotation matrix
  public void Rotate(double rot)
  {
    Matrix3By3 mat = new Matrix3By3();

    double Sin = Math.sin(rot);
    double Cos = Math.cos(rot);

    mat.element11 = Cos;
    mat.element12 = Sin;
    mat.element13 = 0;
    mat.element21 = -Sin;
    mat.element22 = Cos;
    mat.element23 = 0;
    mat.element31 = 0;
    mat.element32 = 0;
    mat.element33 = 1;

    // And multiply
    MatrixMultiply(mat);
  }

  // Create a rotation matrix from a 2D vector
  public void Rotate(Vector2D fwd, Vector2D side)
  {
    Matrix3By3 mat = new Matrix3By3();

    mat.element11 = fwd.x;
    mat.element12 = fwd.y;
    mat.element13 = 0;
    mat.element21 = side.x;
    mat.element22 = side.y;
    mat.element23 = 0;
    mat.element31 = 0;
    mat.element32 = 0;
    mat.element33 = 1;

    // And multiply
    MatrixMultiply(mat);
  }
}
