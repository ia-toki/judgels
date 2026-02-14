import HTMLReactParser from 'html-react-parser';
import { useEffect, useRef } from 'react';
import { renderToString } from 'react-dom/server';

import { useWebPrefs } from '../../modules/webPrefs';
import { HtmlText } from '../HtmlText/HtmlText';
import { SourceCode } from '../SourceCode/SourceCode';

import './RichStatementText.scss';
import 'katex/dist/katex.min.css';

export default function RichStatementText({ children }) {
  const ref = useRef();
  const { isDarkMode } = useWebPrefs();

  const typesetKatex = async () => {
    if (!ref.current) {
      return;
    }

    const { default: renderMathInElement } = await import('katex/dist/contrib/auto-render');
    renderMathInElement(ref.current, {
      delimiters: [
        { left: '\\(', right: '\\)', display: false },
        { left: '$', right: '$', display: false },
        { left: '\\[', right: '\\]', display: true },
      ],
    });
  };

  const containsKatexSyntax = text => {
    const delimiters = ['$', '\\(', '\\)', '\\[', '\\]'];
    for (let delimiter of delimiters) {
      if (text.includes(delimiter)) {
        return true;
      }
    }
    return false;
  };

  useEffect(() => {
    if (containsKatexSyntax(children)) {
      typesetKatex();
    }
  }, [children]);

  let str = children;

  str = str.replace(/<pre data-lang="(.+?)">(.*?)<\/pre>/gs, (_match, lang, code) => {
    return renderToString(
      <SourceCode isDarkMode={isDarkMode} language={lang} showLineNumbers={false}>
        {HTMLReactParser(code.trim())}
      </SourceCode>
    );
  });

  return (
    <div className="rich-statement-text" ref={ref}>
      <HtmlText>{str}</HtmlText>
    </div>
  );
}
