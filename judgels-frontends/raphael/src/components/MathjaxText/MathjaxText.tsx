import React from 'react';
import { HtmlText } from '../HtmlText/HtmlText';

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
    console.log(props.children);
  }

  componentDidMount() {
    if (this.state.containsMathJax) {
      const publicUrl = process.env.PUBLIC_URL;
      this.insertScript('MathJax-config', publicUrl + '/var/conf/mathjax-config.js');
      this.insertScript('MathJax-script', publicUrl + '/mathjax/tex-svg.js');
    }
  }

  componentDidUpdate() {
    if (this.state.containsMathJax) {
      window.MathJax.typeset();
    }
  }

  private insertScript(id: string, url: string) {
    const script = document.createElement('script');

    script.id = id;
    script.src = url;
    script.defer = true;

    document.body.appendChild(script);
  }

  private containsMathjaxSyntax(text: string) {
    const mathjaxDelimitters = ['$', '\\(', '\\)', '\\[', '\\]'];
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
