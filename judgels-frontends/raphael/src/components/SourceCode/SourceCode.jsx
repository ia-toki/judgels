import SyntaxHighlighter, { registerLanguage } from 'react-syntax-highlighter/prism-light';
import c from 'react-syntax-highlighter/languages/prism/c';
import cpp from 'react-syntax-highlighter/languages/prism/cpp';
import go from 'react-syntax-highlighter/languages/prism/go';
import java from 'react-syntax-highlighter/languages/prism/java';
import pascal from 'react-syntax-highlighter/languages/prism/pascal';
import python from 'react-syntax-highlighter/languages/prism/python';
import coy from 'react-syntax-highlighter/styles/prism/coy';

registerLanguage('c', c);
registerLanguage('cpp', cpp);
registerLanguage('go', go);
registerLanguage('java', java);
registerLanguage('pascal', pascal);
registerLanguage('python', python);

export function SourceCode({ language, children }) {
  return (
    <SyntaxHighlighter
      style={coy}
      language={language}
      wrapLines={true}
      showLineNumbers={true}
      lineNumberContainerStyle={{
        backgroundColor: '#f0f0f0',
        float: 'left',
        paddingLeft: '10px',
        paddingRight: '10px',
        marginRight: '5px',
      }}
    >
      {children}
    </SyntaxHighlighter>
  );
}
