import React from 'react';
import { HtmlText } from '../HtmlText/HtmlText';
import './MathjaxText.scss';

declare global {
  interface Window {
    MathJax: any;
  }
}

interface MathjaxTextProps {
  children: string;
}

interface MathjaxTextState {
  containsMathJax: boolean;
}

export class MathjaxText extends React.Component<MathjaxTextProps, MathjaxTextState> {
  constructor(props) {
    super(props);
    this.state = {
      containsMathJax: this.containsMathjaxSyntax(props.children),
    };
  }

  componentDidMount() {
    if (this.state.containsMathJax) {
      this.typesetMathJax();
    }
  }

  componentDidUpdate() {
    if (this.state.containsMathJax) {
      this.typesetMathJax();
    }
  }

  private typesetMathJax() {
    if (window.MathJax) {
      // if the script already exists, typeset directly.
      window.MathJax.typeset();
    } else {
      // otherwise setup the mathjax.
      const publicUrl = process.env.PUBLIC_URL;
      this.setupMathjaxConfig();
      this.insertScript('MathJax-script', publicUrl + '/mathjax/tex-chtml.js');
    }
  }

  private setupMathjaxConfig() {
    window.MathJax = {
      tex: {
        inlineMath: [
          ['$$$', '$$$'],
          ['\\(', '\\)'],
        ],
        displayMath: [
          ['$$$$', '$$$$'],
          ['\\[', '\\]'],
        ],
      },
      svg: {
        fontCache: 'global',
      },
    };
  }

  private insertScript(id: string, url: string) {
    const script = document.createElement('script');

    script.id = id;
    script.src = url;
    script.defer = true;

    document.head.appendChild(script);
  }

  private containsMathjaxSyntax(text: string) {
    const mathjaxDelimitters = ['$$$', '$$$$', '\\(', '\\)', '\\[', '\\]'];
    for (let delimitter of mathjaxDelimitters) {
      if (text.includes(delimitter)) {
        return true;
      }
    }
    return false;
  }

  render() {
    return (
      <div className="mathjax-wrapper">
        <HtmlText>{this.props.children}</HtmlText>
      </div>
    );
  }
}
