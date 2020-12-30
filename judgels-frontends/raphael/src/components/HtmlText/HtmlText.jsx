import HTMLReactParser from 'html-react-parser';
import { Component } from 'react';

// CSS definition is in index.scss. See https://github.com/webpack-contrib/mini-css-extract-plugin/issues/250

export class HtmlText extends Component {
  ref = null;

  componentDidMount() {
    this.setUpSpoilers();
  }

  render() {
    return (
      <div className="html-text" ref={this.createRef}>
        {HTMLReactParser(this.props.children)}
      </div>
    );
  }

  createRef = e => {
    this.ref = e;
  };

  setUpSpoilers() {
    const spoilers = this.ref.getElementsByClassName('spoiler');
    for (let i = 0; i < spoilers.length; i++) {
      const spoiler = spoilers[i];
      spoiler.onclick = function() {
        const content = this.getElementsByTagName('div');
        if (content.length > 0) {
          content[0].style.display = content[0].style.display === 'block' ? 'none' : 'block';
        }
      };
    }
  }
}
