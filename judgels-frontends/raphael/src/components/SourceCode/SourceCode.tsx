import * as React from 'react';
import SyntaxHighlighter, { registerLanguage } from 'react-syntax-highlighter/prism-light';
import c from 'react-syntax-highlighter/languages/prism/c';
import cpp from 'react-syntax-highlighter/languages/prism/cpp';
import java from 'react-syntax-highlighter/languages/prism/java';
import pascal from 'react-syntax-highlighter/languages/prism/pascal';
import python from 'react-syntax-highlighter/languages/prism/python';
import coy from 'react-syntax-highlighter/styles/prism/coy';

registerLanguage('c', c);
registerLanguage('cpp', cpp);
registerLanguage('java', java);
registerLanguage('pascal', pascal);
registerLanguage('python', python);

export interface SourceCodeProps {
  language: string;
  children?: any;
}

const customStyle = {
  padding: '5px',
};

export const SourceCode = (props: SourceCodeProps) => (
  <SyntaxHighlighter style={coy} customStyle={customStyle} language={props.language}>
    {props.children}
  </SyntaxHighlighter>
);
