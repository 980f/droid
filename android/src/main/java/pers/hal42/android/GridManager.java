package pers.hal42.android;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.TextView;

import java.lang.reflect.Constructor;

/**
 * Copyright andyh  10/17/12 3:55 PM
 * todo: "clear screen" and reset cursor.
 */
public class GridManager extends GridLayout {

  /**
   * grid position generator.
   * While public there is typically only one instance per GridManager and that instance is part of the GridManager object.
   * As a consequence of making row and col members public their values are checked and constrained before each use herein.
   */
  public class Cursor {
    public int col;
    public int row;
    public GridLayout grid;//for maxcol or maxrow logic

    public Cursor(GridLayout grid) {
      this.grid = grid;
      col = 0;
      row = 0;
    }

    /**
     * @param span number of desired cells.
     * @return layout for given span, except if <0 or would exceed end of line then remaining on line
     */
    public LayoutParams next(int span) {
      final int columnCount = grid.getColumnCount();
      if (col >= columnCount) { //wrap line
        col = 0;
        ++row;
      }
      if (span < 0 ||//hack for when it is inconvenient to call eol() due to default args.
          span > columnCount - col) { //do not wrap middle of entity nor auto expand grid.
        span = columnCount - col;//and if that is negative let it blow for now
      }
      GridLayout.LayoutParams layout = new GridLayout.LayoutParams(spec(row), spec(col, span));
      col += span;
      return layout;
    }

    public LayoutParams next() {
      return next(1);
    }

    /**
     * @return layout of remaining cells in this row
     */
    public LayoutParams eol() {
      return next(-1);
    }

  }

  public Cursor cursor;

  public GridManager(Context context) {
    super(context);
    cursor = new Cursor(this);
  }

  public <K extends View> K add(Class<K> viewClass, int span, boolean fillWidth) throws IllegalArgumentException {
    try {
      Constructor<K> ctor = viewClass.getConstructor(Context.class);
      K viewObject = ctor.newInstance(getContext());

      GridLayout.LayoutParams layout = cursor.next(span);
      if (fillWidth) {
        layout.setGravity(Gravity.FILL_HORIZONTAL);
      }
      super.addView(viewObject, layout);//
      return viewObject;
    } catch (Exception e) {
      e.printStackTrace();
      throw new IllegalArgumentException(this.getClass().getName(), e);
    }
  }

  public <K extends View> K add(Class<K> viewClass) throws IllegalArgumentException {
    return add(viewClass, 1, false);
  }

  public <K extends View> K add(Class<K> viewClass, int span) {
    return add(viewClass, span, false);
  }

  public <K extends View> K add(Class<K> viewClass, boolean fillWidth) throws IllegalArgumentException {
    return add(viewClass, 1, fillWidth);
  }

  public TextView addDisplay(CharSequence fixedText) {
    return addDisplay(fixedText, -1);
  }

  public TextView addDisplay(CharSequence fixedText, int span) {
    TextView child = add(TextView.class, span, true); //true: text boxes should fill their space so that they don't change shape as much
    child.setBackgroundColor(Color.WHITE); //alh: should figure out how to do style sheet like stuff. til then hardcode personal prefs.
    child.setTextColor(Color.BLACK);
    child.setText(fixedText);
    return child;
  }

  public EditText addTextEntry(CharSequence prompt) {
    EditText editor = add(EditText.class);
    editor.setText(prompt);
    return editor;
  }

  @Deprecated //not yet finished
  public EditText addNumberEntry(float initialValue) {
    return addNumberEntry(initialValue, -1);
  }

  @Deprecated //not yet finished
  public EditText addNumberEntry(float initialValue, int span) {
    EditText editor = add(EditText.class, span);
    //todo: set input constraint so that number keyboard is invoked.
    editor.setText(String.valueOf(initialValue));
    return editor;
  }

  public Button addButton(CharSequence legend) {
    return addButton(legend, 1);
  }

  public Button addButton(CharSequence legend, int span) {
    Button button = add(Button.class, span, true);//true: typically want buttons to be as big as possible for fat fingers.
    button.setText(legend);
    return button;
  }

  public Button addButton(CharSequence legend, OnClickListener action) {
    return addButton(legend, 1, action);
  }

  public Button addButton(CharSequence legend, int span, OnClickListener action) {
    Button button = addButton(legend, span);
    if (action != null) {
      button.setOnClickListener(action);
    }
    return button;
  }

  public ActivityLauncher addLauncher(CharSequence legend, Class<? extends Activity> cls) {
    ActivityLauncher button = add(ActivityLauncher.class);
    button.setActivity(cls);
    button.setText(legend);
    return button;
  }

  public LinearSlider addSlider() {
    return addSlider(-1);
  }

  public LinearSlider addSlider(int span) {
    return add(LinearSlider.class, span, true);
  }

}
