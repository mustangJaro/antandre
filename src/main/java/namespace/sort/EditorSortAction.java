package namespace.sort;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;

public class EditorSortAction extends AnAction {

    /**
     * Returns an ArrayList of required namespaces given a fully-formed
     * require statement.
     *
     * @param requireStatement
     * @return
     */
    private ArrayList<String> getRequiredNamespaces(String requireStatement){
        ArrayList<String> requires = new ArrayList<>();
        int startSearchIndex = 0;
        String partialRequire = requireStatement;
        while(true){
            int openBracketLoc = partialRequire.indexOf("[", startSearchIndex);
            if(openBracketLoc < 0)
                break;
            partialRequire = partialRequire.substring(openBracketLoc);
            StringBuilder require = new StringBuilder();
            boolean foundEndOfRequire = false;
            int closeBracketLoc = 0, openBrackets = 0;
            while(!foundEndOfRequire){
                char c = partialRequire.charAt(closeBracketLoc);
                if (c == '[')
                    openBrackets++;
                if (c == ']')
                    openBrackets--;
                require.append(c);
                if (openBrackets == 0)
                    foundEndOfRequire = true;
                closeBracketLoc++;
            }
            requires.add(require.toString());
            startSearchIndex = closeBracketLoc;
        }

        return requires;
    }
    /**
     * Sorts the namespaces within the `:require` reference in the `ns` macro
     * @param e  Event related to this action
     */
    @Override
    public void actionPerformed(@NotNull final AnActionEvent e) {
        final Editor editor = e.getRequiredData(CommonDataKeys.EDITOR);
        final Project project = e.getRequiredData(CommonDataKeys.PROJECT);
        final Document document = editor.getDocument();

        WriteCommandAction.runWriteCommandAction(project, () ->
                {
                    CharSequence seq = document.getCharsSequence();
                    StringBuilder namespace = new StringBuilder();
                    boolean foundEndOfNs = false;
                    int i = 0, openParans = 0, indexOfNamespace = -1;
                    // TODO(AJ) There is something not parsing properly here when there is the reader conditional
                    while (!foundEndOfNs) {
                        char c = seq.charAt(i);
                        if (c == '(') {
                            openParans++;
                            if (indexOfNamespace == -1)
                                indexOfNamespace = i;
                        } else if (c == ')')
                            openParans--;
                        namespace.append(c);
                        if (i > 0 && openParans == 0)
                            foundEndOfNs = true;
                        i++;
                    }

                    //Get the require statement within the namespace declaration
                    int subIndexOfRequire = namespace.indexOf("(:require");
                    int subIndexOfEndRequire = namespace.indexOf(")", subIndexOfRequire) + 1;
                    String requireStmt = namespace.substring(subIndexOfRequire, subIndexOfEndRequire);

                    //Extract and sort the namespaces out of the require statement
                    ArrayList<String> requiredNamespaces = getRequiredNamespaces(requireStmt);
                    requiredNamespaces.sort(null);

                    // Convert ArrayList back to String
                    StringBuilder requiredString = new StringBuilder("(:require");
                    for (String ns : requiredNamespaces){
                        requiredString
                                .append("\n   ")
                                .append(ns); // TODO(AJ) Add proper spacing here DocumentUtil
                    }
                    requiredString.append(")");

                    document.replaceString(
                            indexOfNamespace + subIndexOfRequire,
                            indexOfNamespace + subIndexOfEndRequire,
                            requiredString);
                }
        );
    }

    ArrayList<String> extensions = new ArrayList<String>(Arrays.asList("clj", "cljc", "cljs"));
    /**
     * Sets visibility and enables this action menu item if:
     *   A project is open,
     *   An editor is active,
     *   A Clojure/ClojureScript file is active
     * @param e  Event related to this action
     */
    @Override
    public void update(@NotNull final AnActionEvent e) {
        // Get required data keys
        final Project project = e.getProject();
        final Editor editor = e.getData(CommonDataKeys.EDITOR);
        VirtualFile file = FileDocumentManager.getInstance().getFile(editor.getDocument());

        // Set visibility and enable only in case of existing project and editor and if a selection exists
        e.getPresentation().setEnabledAndVisible( project != null && editor != null && extensions.contains(file.getExtension()));
    }
}
