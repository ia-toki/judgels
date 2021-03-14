import { Checkbox } from '@blueprintjs/core';
import { push } from 'connected-react-router';
import { Component } from 'react';
import { connect } from 'react-redux';
import { withRouter } from 'react-router';
import { parse, stringify } from 'query-string';

import { ContentCard } from '../../../../components/ContentCard/ContentCard';
import * as problemActions from '../modules/problemActions';

import './ProblemTagFilter.css';

class ProblemTagFilter extends Component {
  state;

  constructor(props) {
    super(props);

    const queries = parse(this.props.location.search);
    const tags = this.parseTags(queries.tags);

    this.state = {
      tags,
      response: undefined,
    };
  }

  async componentDidMount() {
    const response = await this.props.onGetProblemTags();
    const allTags = [].concat(response.data.map(c => c.options.map(opt => opt.value))).flat();

    this.setState({ response, allTags });
  }

  render() {
    return (
      <ContentCard>
        <h4>Filter problem</h4>
        <hr />
        {this.renderAvailableTags()}
      </ContentCard>
    );
  }

  renderAvailableTags = () => {
    const { response } = this.state;
    if (!response) {
      return null;
    }

    const { data: problemTags } = response;
    return (
      <>
        {problemTags.map(category => this.renderTagCategory(category))}
        <h5>Topic</h5>
        <small>coming soon...</small>
      </>
    );
  };

  renderTagCategory = ({ title, options }) => {
    return (
      <div key={title}>
        <h5 className="problem-tag-filter__category">{title}</h5>
        {options.map(opt => (
          <Checkbox
            key={opt.value}
            name={opt.value}
            className="problem-tag-filter__option"
            label={opt.label}
            checked={this.isTagSelected(opt.value)}
            onChange={this.changeTag}
          />
        ))}
      </div>
    );
  };

  isTagSelected = tag => {
    return this.state.tags.includes(tag);
  };

  changeTag = e => {
    const tag = e.target.name;
    const checked = e.target.checked;

    let tags = this.state.tags;
    if (checked) {
      tags = [...new Set([...tags, tag])];
    } else {
      let s = new Set(tags);
      s.delete(tag);
      tags = [...s];
    }

    tags = this.sanitizeTags(tags);

    const queries = parse(this.props.location.search);
    this.props.onPush({
      search: stringify({
        ...queries,
        tags,
        page: 1,
      }),
    });

    this.setState({ tags });
  };

  parseTags = queryTags => {
    let tags = queryTags || [];
    if (typeof tags === 'string') {
      tags = [tags];
    }
    return tags;
  };

  sanitizeTags = tags => {
    return tags.filter(t => this.state.allTags.includes(t));
  };
}

const mapDispatchToProps = {
  onPush: push,
  onGetProblemTags: problemActions.getProblemTags,
};
export default withRouter(connect(undefined, mapDispatchToProps)(ProblemTagFilter));
