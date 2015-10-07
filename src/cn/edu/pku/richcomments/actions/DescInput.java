/*
 *  Examples on using SWT Controls.
 *
 * Created on Mar 28, 2005
 *
 */
package cn.edu.pku.richcomments.actions;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import cn.edu.pku.richcomments.dom.RCDesc;

/**
 * Basic SWT Controls example.
 * Shows use of basic input controls like Text and Buttons.
 * Shows basic LayoutManager use.
 *
 * @author barryf
 */
public class DescInput extends Composite {

    private static final String[] TYPE_STRING = new String[]{"image","video","animated gif","audio","unknown"};
	// input fields;  members so they can be referenced from event handlers
    Text titleField;
    Text typeField;
    Text locField;
    
    Composite superParent;
    /**
     * Constructor.
     */
    protected DescInput(Composite parent) {
        this(parent, SWT.NONE);  // must always supply parent
    }
    /**
     * Constructor.
     */
    protected DescInput(Composite parent, int style) {
        super(parent, style);   // must always supply parent and style
        superParent = parent;
        createGui();
    }

    // GUI creation helpers

    protected Text createLabelledText(Composite parent, String label) {
        return createLabelledText(parent, label, 20, null);
    }
    protected Text createLabelledText(Composite parent, String label, int limit, String tip) {
        Label l = new Label(parent, SWT.LEFT);
        l.setText(label);
        Text text  = new Text(parent, SWT.SINGLE);
        if (limit > 0) {
            text.setTextLimit(limit);
        }
        if (tip != null) {
            text.setToolTipText(tip);
        }
        text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        return text;
    }

    protected Button createButton(Composite parent, String label, SelectionListener l) {
        return createButton(parent, label, l);
    }
    protected Button createButton(Composite parent, String label, String tip, SelectionListener l) {
        Button b = new Button(parent, SWT.NONE);
        b.setText(label);
        if (tip != null) {
            b.setToolTipText(tip);
        }
        if (l != null) {
            b.addSelectionListener(l);
        }
        return b;
    }

    // partial selection listener
    class MySelectionAdapter implements SelectionListener {
        public void widgetSelected(SelectionEvent e) {
            // default is to do nothing
        }
        public void widgetDefaultSelected(SelectionEvent e) {
            widgetSelected(e);
        }
    };
    
    protected RCDesc returnVal;
    
    public static RCDesc getDesc(){
        // the display allows access to the host display device
        final Display display = Display.getDefault();

        // the shell is the top level window (AKA frame)
        final Shell shell = new Shell(display);
        shell.setText("Insert RC Descriptor");   // Give me a title

        // all children split space equally
        shell.setLayout( new FillLayout());

        DescInput basic = new DescInput(shell);

        shell.pack();       // make shell take minimum size
        shell.open();       // open shell for user access

        // process all user input events until the shell is disposed (i.e., closed)
        while ( !shell.isDisposed()) {
            if (!display.readAndDispatch()) {  // process next message
                display.sleep();              // wait for next message
            }
        }
        return basic.returnVal;
    }
    
    protected void createGui() {
        setLayout(new GridLayout(1, true));

        // create the input area

        Group entryGroup = new Group(this, SWT.NONE);
        entryGroup.setText("Input Values");
        // use 2 columns, not same width
        GridLayout entryLayout = new GridLayout(2, false);
        entryGroup.setLayout(entryLayout);
        entryGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        titleField    = createLabelledText(entryGroup, "Title: ", 0, "Enter the title");
        typeField    = createLabelledText(entryGroup, "Type: ", 0, "Enter the type");
        locField   = createLabelledText(entryGroup, "Location: ", 0, "Enter the url");

        // create the button area

        Composite buttons = new Composite(this, SWT.NONE);
        buttons.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        // make all buttons the same size
        FillLayout buttonLayout = new FillLayout();
        buttonLayout.marginHeight = 2;
        buttonLayout.marginWidth = 2;
        buttonLayout.spacing = 5;
        buttons.setLayout(buttonLayout);

        // OK button prints input values
        Button okButton = createButton(buttons, "&Ok", "Process input",
                                       new MySelectionAdapter() {
                                           public void widgetSelected(SelectionEvent e) {
                                        	   returnVal = new RCDesc(titleField.getText()
                                        			   , typeField.getText()
                                        			   , locField.getText());
                                        	   if (check(returnVal))
                                        		   superParent.dispose();
                                           }

										private boolean check(RCDesc desc) {
											if (desc.title.indexOf(';')!=-1){
												MessageBox msg = new MessageBox((Shell) superParent, SWT.OK);
												msg.setText("Error");
												msg.setMessage("character ';' is not allowed in title");
												msg.open();
												return false;
											}
											if (desc.type.indexOf(';')!=-1){
												MessageBox msg = new MessageBox((Shell) superParent, SWT.OK);
												msg.setText("Error");
												msg.setMessage("character ';' is not allowed in type");
												msg.open();
												return false;
											}
											if (desc.url.indexOf(';')!=-1){
												MessageBox msg = new MessageBox((Shell) superParent, SWT.OK);
												msg.setText("Error");
												msg.setMessage("character ';' is not allowed in location");
												msg.open();
												return false;
											}
											if (desc.url.indexOf("://")==-1){
												MessageBox msg = new MessageBox((Shell) superParent, SWT.YES|SWT.NO);
												msg.setText("Warnning");
												msg.setMessage("It seems the url does not indicate a proper protocol. Proceed anyway?");
												int ret = msg.open();
												return ret==SWT.YES;
											}
											if (desc.url.startsWith("file://")){
												MessageBox msg = new MessageBox((Shell) superParent, SWT.YES|SWT.NO);
												msg.setText("Warnning");
												msg.setMessage("this is a local file, and it has to be copied to source directory. Do you wish to continue?");
												int ret = msg.open();
												return ret==SWT.YES;
											}
											return true;
										}
                                       });
        // open a local file
        Button localButton = createButton(buttons, "Open &Local File...", "Open a local file",
                new MySelectionAdapter() {
                    public void widgetSelected(SelectionEvent e) {
                    	FileDialog dialog = new FileDialog((Shell) superParent,SWT.OPEN);
						dialog.setFilterNames(new String[] { "Images","Videos"
								,"Animated GIFs","Audios",
								"All Files (*.*)" });
						dialog.setFilterExtensions(new String[] { "*.jpg;*.png;*.bmp;*.jpeg",
								"*.mp4;*.avi;*.mov;*.rm;*.rmvb;*.mpg;*.flv",
								"*.gif","*.mp3;*.rm;*.acc;*.wav",
								"*.*" }); // Windows wild cards
						String path = dialog.open();
						if (path==null)
							return;
						int i = dialog.getFilterIndex();
						titleField.setText(dialog.getFileName());
						typeField.setText(TYPE_STRING[i]);
						locField.setText("file://" + path);
                    }
                });
    }

    /**
     * Main driver program.
     */
    public static void main(String[] args) {
    	System.out.println(getDesc());
    }
}

