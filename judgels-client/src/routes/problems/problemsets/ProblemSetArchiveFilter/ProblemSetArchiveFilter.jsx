import { Radio, RadioGroup } from '@blueprintjs/core';
import classNames from 'classnames';
import { push } from 'connected-react-router';
import { parse, stringify } from 'query-string';
import { Component } from 'react';
import { connect } from 'react-redux';
import { withRouter } from 'react-router';

import { ContentCard } from '../../../../components/ContentCard/ContentCard';
import { sendGAEvent } from '../../../../ga';

import * as archiveActions from '../modules/archiveActions';

import './ProblemSetArchiveFilter.scss';

class ProblemSetArchiveFilter extends Component {
  state;

  constructor(props) {
    super(props);

    const queries = parse(this.props.location.search);
    const archiveSlug = queries.archive || '';

    this.state = {
      response: undefined,
      archiveSlug,
    };
  }

  async componentDidMount() {
    const response = await this.props.onGetArchives();
    this.setState({ response });
  }

  render() {
    const { response } = this.state;
    if (!response) {
      return null;
    }
    return (
      <ContentCard>
        <h4>Filter problemset</h4>
        <hr />
        {this.renderArchiveCategories()}
      </ContentCard>
    );
  }

  renderArchiveCategories = () => {
    const archives = [{ slug: '', name: '(All problemsets)', category: '' }, ...this.state.response.data];
    const archivesByCategory = {};
    archives.forEach(archive => {
      if (archivesByCategory[archive.category]) {
        archivesByCategory[archive.category] = [...archivesByCategory[archive.category], archive];
      } else {
        archivesByCategory[archive.category] = [archive];
      }
    });

    const categories = Object.keys(archivesByCategory).sort();
    return categories.map(category => this.renderArchives(category, archivesByCategory[category]));
  };

  renderArchives = (category, archives) => {
    return (
      <div key={category}>
        {category && <p className="archive-filter__category">{category}</p>}
        <RadioGroup
          key={category}
          name="archiveSlug"
          onChange={this.changeArchive}
          selectedValue={this.state.archiveSlug}
        >
          {archives.map(archive => (
            <Radio key={archive.slug} labelElement={this.renderArchiveOption(archive)} value={archive.slug} />
          ))}
        </RadioGroup>
      </div>
    );
  };

  renderArchiveOption = archive => {
    return (
      <span className={classNames({ 'archive-filter__option--inactive': archive.slug !== this.state.archiveSlug })}>
        {archive.name}
      </span>
    );
  };

  changeArchive = e => {
    const archiveSlug = e.target.value;
    const queries = parse(this.props.location.search);
    this.props.onPush({
      search: stringify({
        ...queries,
        name: undefined,
        page: undefined,
        archive: archiveSlug === '' ? undefined : archiveSlug,
      }),
    });
    this.setState({ archiveSlug });

    sendGAEvent({
      category: 'Problems',
      action: 'Filter archive',
      label: archiveSlug,
    });
  };
}

const mapDispatchToProps = {
  onGetArchives: archiveActions.getArchives,
  onPush: push,
};
export default withRouter(connect(undefined, mapDispatchToProps)(ProblemSetArchiveFilter));
