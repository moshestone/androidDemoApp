package androidx.constraintlayout.solver.widgets;

public class Rectangle {
    public int height;
    public int width;
    public int x;
    public int y;

    public void setBounds(int x2, int y2, int width2, int height2) {
        this.x = x2;
        this.y = y2;
        this.width = width2;
        this.height = height2;
    }

    /* access modifiers changed from: 0000 */
    public void grow(int w, int h) {
        this.x -= w;
        this.y -= h;
        this.width += w * 2;
        this.height += h * 2;
    }

    /* access modifiers changed from: 0000 */
    public boolean intersects(Rectangle bounds) {
        int i = this.x;
        int i2 = bounds.x;
        if (i >= i2 && i < i2 + bounds.width) {
            int i3 = this.y;
            int i4 = bounds.y;
            if (i3 >= i4 && i3 < i4 + bounds.height) {
                return true;
            }
        }
        return false;
    }

    public boolean contains(int x2, int y2) {
        int i = this.x;
        if (x2 >= i && x2 < i + this.width) {
            int i2 = this.y;
            if (y2 >= i2 && y2 < i2 + this.height) {
                return true;
            }
        }
        return false;
    }

    public int getCenterX() {
        return (this.x + this.width) / 2;
    }

    public int getCenterY() {
        return (this.y + this.height) / 2;
    }
}
