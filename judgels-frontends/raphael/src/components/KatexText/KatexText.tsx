import React from 'react';

import { HtmlText } from '../HtmlText/HtmlText';

import './KatexText.scss';

declare global {
  interface Window {
    renderMathInElement: any;
  }
}

interface KatexTextProps {
  children: string;
}

interface KatexTextState {
  containsKatex: boolean;
}

export class KatexText extends React.Component<KatexTextProps, KatexTextState> {
  ref;

  constructor(props) {
    super(props);
    this.state = {
      containsKatex: this.containsKatexSyntax(props.children),
    };
    this.ref = React.createRef();
  }

  componentDidMount() {
    if (this.state.containsKatex) {
      this.typesetKatex();
    }
  }

  componentDidUpdate() {
    if (this.state.containsKatex) {
      this.typesetKatex();
    }
  }

  private async typesetKatex() {
    await require('katex/dist/katex.min.css');
    const { default: renderMathInElement } = await import('katex/dist/contrib/auto-render');
    renderMathInElement(this.ref.current, {
      delimiters: [
        { left: '\\(', right: '\\)', display: false },
        { left: '$', right: '$', display: false },
        { left: '\\[', right: '\\]', display: true },
        { left: '$$', right: '$$', display: true },
      ],
    });
  }

  private containsKatexSyntax(text: string) {
    const delimiters = ['$$$', '$$$$', '\\(', '\\)', '\\[', '\\]'];
    for (let delimiter of delimiters) {
      if (text.includes(delimiter)) {
        return true;
      }
    }
    return false;
  }

  render() {
    return (
      <div className="katex-wrapper" ref={this.ref}>
        <HtmlText>{this.props.children}</HtmlText>
      </div>
    );
  }
}
