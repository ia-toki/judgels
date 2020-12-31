import { createRef, Component } from 'react';

import { HtmlText } from '../HtmlText/HtmlText';

import './KatexText.scss';

export class KatexText extends Component {
  ref;

  constructor(props) {
    super(props);
    this.state = {
      containsKatex: this.containsKatexSyntax(props.children),
    };
    this.ref = createRef();
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

  async typesetKatex() {
    await require('katex/dist/katex.min.css');
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
    return (
      <div className="katex-wrapper" ref={this.ref}>
        <HtmlText>{this.props.children}</HtmlText>
      </div>
    );
  }
}
