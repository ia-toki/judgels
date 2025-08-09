import HTMLReactParser from 'html-react-parser';
import { Component, createRef } from 'react';
import { renderToString } from 'react-dom/server';
import { connect } from 'react-redux';

import { selectIsDarkMode } from '../../modules/webPrefs/webPrefsSelectors';
import { HtmlText } from '../HtmlText/HtmlText';
import { SourceCode } from '../SourceCode/SourceCode';

import './RichStatementText.scss';
import 'katex/dist/katex.min.css';

export class RichStatementText extends Component {
  ref;

  constructor(props) {
    super(props);
    this.ref = createRef();
  }

  componentDidMount() {
    if (this.containsKatexSyntax(this.props.children)) {
      this.typesetKatex();
    }
  }

  componentDidUpdate(prevProps) {
    if (this.props.key !== prevProps.key) {
      if (this.containsKatexSyntax(this.props.children)) {
        this.typesetKatex();
      }
    }
  }

  async typesetKatex() {
    if (!this.ref.current) {
      return;
    }

    const { default: renderMathInElement } = await import('katex/dist/contrib/auto-render');
    renderMathInElement(this.ref.current, {
      delimiters: [
        { left: '\\(', right: '\\)', display: false },
        { left: '$', right: '$', display: false },
        { left: '\\[', right: '\\]', display: true },
      ],
    });
  }

  containsKatexSyntax(text) {
    const delimiters = ['$', '\\(', '\\)', '\\[', '\\]'];
    for (let delimiter of delimiters) {
      if (text.includes(delimiter)) {
        return true;
      }
    }
    return false;
  }

  render() {
    const { isDarkMode, children } = this.props;

    let str = children;

    str = str.replace(/<pre data-lang="(.+?)">(.*?)<\/pre>/gs, (match, lang, code) => {
      return renderToString(
        <SourceCode isDarkMode={isDarkMode} language={lang} showLineNumbers={false}>
          {HTMLReactParser(code.trim())}
        </SourceCode>
      );
    });

    return (
      <div className="rich-statement-text" ref={this.ref}>
        <HtmlText>{str}</HtmlText>
      </div>
    );
  }
}
const mapStateToProps = state => ({
  isDarkMode: selectIsDarkMode(state),
});

export default connect(mapStateToProps)(RichStatementText);
