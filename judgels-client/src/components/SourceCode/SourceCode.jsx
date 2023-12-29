import { connect } from 'react-redux';

import c from 'react-syntax-highlighter/languages/prism/c';
import cpp from 'react-syntax-highlighter/languages/prism/cpp';
import go from 'react-syntax-highlighter/languages/prism/go';
import java from 'react-syntax-highlighter/languages/prism/java';
import pascal from 'react-syntax-highlighter/languages/prism/pascal';
import python from 'react-syntax-highlighter/languages/prism/python';
import rust from 'react-syntax-highlighter/languages/prism/rust';
import SyntaxHighlighter, { registerLanguage } from 'react-syntax-highlighter/prism-light';
import coy from 'react-syntax-highlighter/styles/prism/coy';
import tomorrow from 'react-syntax-highlighter/styles/prism/tomorrow';

import { selectIsDarkMode } from '../../modules/webPrefs/webPrefsSelectors';

import './SourceCode.scss';

registerLanguage('c', c);
registerLanguage('cpp', cpp);
registerLanguage('go', go);
registerLanguage('java', java);
registerLanguage('pascal', pascal);
registerLanguage('python', python);
registerLanguage('rust', rust);

export function SourceCode({ isDarkMode, language, showLineNumbers = true, children }) {
  return (
    <SyntaxHighlighter
      className="source-code"
      style={isDarkMode ? tomorrow : coy}
      language={language}
      wrapLines={true}
      showLineNumbers={showLineNumbers}
      lineNumberContainerStyle={{
        backgroundColor: isDarkMode ? '#394B59' : '#f0f0f0',
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

const mapStateToProps = state => ({
  isDarkMode: selectIsDarkMode(state),
});

export default connect(mapStateToProps)(SourceCode);
