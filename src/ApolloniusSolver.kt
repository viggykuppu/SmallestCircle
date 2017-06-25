class ApolloniusSolver{

    companion object {
        fun Solve(x: Circle, y: Circle, z: Circle): Circle{
            val a1 = 2 * (x.x - y.x)
            val b1 = 2 * (x.y - y.y)
            val c1 = 2 * (x.r - y.r)
            val d1 = (x.x*x.x + x.y*x.y - x.r*x.r) - (y.x*y.x + y.y*y.y - y.r*y.r)

            val a2 = 2 * (x.x - z.x)
            val b2 = 2 * (x.y - z.y)
            val c2 = 2 * (x.r - z.r)
            val d2 = (x.x*x.x + x.y*x.y - x.r*x.r) - (z.x*z.x + z.y*z.y - z.r*z.r)

            val a = (b2*d1 - b1*d2).toDouble()
            val b = (b1*c2 - b2*c1).toDouble()
            val c = (a1*b2 - b1*a2).toDouble()
            val d = (a1*d2 - a2*d1).toDouble()
            val e = (a2*c1 - a1*c2).toDouble()
            val f = (a1*b2 - a2*b1).toDouble()
            val x1 = x.x.toDouble()
            val y1 = x.y.toDouble()
            val r1 = x.r.toDouble()

            val r_0 = x1*x1 + y1*y1 - r1*r1 + ((a*a)/(c*c)) + ((d*d)/(f*f)) - 2*a*x1/c - 2*d*y1/c
            val r_1 = -2*r1 + 2*a*b/(c*c) + 2*d*e/(f*f) - 2*b*x1/c - 2*e*y1/f
            val r_2 = ((b*b)/(c*c) + (e*e)/(f*f) - 1)

            val r_c = Math.ceil(QuadraticSolver(r_2,r_1,r_0)).toInt()

            val x_c = Math.ceil((a + b*r_c)/c).toInt()
            val y_c = Math.ceil((d + e*r_c)/f).toInt()


            return Circle(x_c, y_c, r_c)
        }

        fun QuadraticSolver(a: Double, b: Double, c: Double): Double{
            val determinant = Math.sqrt((b*b - 4*a*c))

            return (-b + determinant)/(2*a)
        }

    }
}
