import { Classes } from '@blueprintjs/core';
import { Component } from 'react';

import { ContentCard } from '../ContentCard/ContentCard';

export class LoadingContentCard extends Component {
  timer;

  state = {
    showSkeleton: false,
  };

  componentDidMount() {
    this.timer = setTimeout(() => {
      this.setState({ showSkeleton: true });
      this.timer = 0;
    }, 200);
  }

  componentWillUnmount() {
    if (this.timer) {
      clearTimeout(this.timer);
      this.timer = 0;
    }
  }

  render() {
    if (!this.state.showSkeleton) {
      return null;
    }
    return (
      <ContentCard>
        <h4 className={Classes.SKELETON}>This is a placeholder for a long content name</h4>
        <p className={Classes.SKELETON}>
          <small>Placeholder for content description</small>
        </p>
      </ContentCard>
    );
  }
}
