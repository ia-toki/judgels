import HTMLReactParser from 'html-react-parser';
import render from 'preact-render-to-string';
import { Component } from 'react';
import { StaticRouter } from 'react-router';

import { UserRef } from '../UserRef/UserRef';

// CSS definition is in index.scss. See https://github.com/webpack-contrib/mini-css-extract-plugin/issues/250

export class HtmlText extends Component {
  ref = null;

  componentDidMount() {
    this.setUpSpoilers();
  }

  render() {
    const { children, profilesMap } = this.props;

    let str = children;
    if (profilesMap) {
      Object.keys(profilesMap).forEach(userJid => {
        const profile = profilesMap[userJid];
        str = str.replace(
          '[user:' + profile.username + ']',
          render(
            <StaticRouter>
              <UserRef profile={profile} />
            </StaticRouter>
          )
        );
      });
    }

    return (
      <div className="html-text" ref={this.createRef}>
        {HTMLReactParser(str)}
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
