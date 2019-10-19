import React from 'react';
import { HtmlText } from '../HtmlText/HtmlText';

declare global {
  interface Window {
    MathJax: any;
  }
}

interface MathjaxProps {
  text: string;
}
export class MathjaxWrapper extends React.Component<MathjaxProps> {
  isContainMathJax = false;

  constructor(props) {
    super(props);
    const { text } = this.props;

    this.isContainMathJax = this.isContainMathJaxSyntax(text);
  }

  componentDidMount() {
    if (this.isContainMathJax) {
      const publicUrl = process.env.PUBLIC_URL;
      this.insertScript('MathJax-config', publicUrl + '/var/conf/mathjax-config.js');
      this.insertScript('MathJax-script', publicUrl + '/mathjax/tex-svg.js');
    }
  }

  componentDidUpdate() {
    if (this.isContainMathJax) {
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

  private isContainMathJaxSyntax(text: string) {
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
        <HtmlText>{this.props.text}</HtmlText>
      </div>
    );
  }
}
