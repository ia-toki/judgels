import HTMLReactParser from 'html-react-parser';
import { Component } from 'react';
import { renderToString } from 'react-dom/server';

import { UserRef } from '../UserRef/UserRef';

import './HtmlText.scss';

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
        str = str.replace('[user:' + profile.username + ']', renderToString(<UserRef profile={profile} useAnchor />));
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
      spoiler.onclick = function () {
        const content = this.getElementsByTagName('div');
        if (content.length > 0) {
          content[0].style.display = content[0].style.display === 'block' ? 'none' : 'block';
        }
      };
    }
  }
}
