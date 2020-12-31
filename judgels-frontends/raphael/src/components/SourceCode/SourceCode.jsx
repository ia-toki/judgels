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

const customStyle = {
  padding: '5px',
};

export function SourceCode({ language, children }) {
  return (
    <SyntaxHighlighter style={coy} customStyle={customStyle} language={language}>
      {children}
    </SyntaxHighlighter>
  );
}
