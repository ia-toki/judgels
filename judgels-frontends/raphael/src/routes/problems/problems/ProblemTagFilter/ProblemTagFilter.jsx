import { Checkbox } from '@blueprintjs/core';
import classNames from 'classnames';
import { push } from 'connected-react-router';
import { Component } from 'react';
import { connect } from 'react-redux';
import { withRouter } from 'react-router';
import { parse, stringify } from 'query-string';

import { ContentCard } from '../../../../components/ContentCard/ContentCard';
import * as problemActions from '../modules/problemActions';

import './ProblemTagFilter.scss';

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
    return problemTags.map(category => this.renderTagCategory(category));
  };

  renderTagCategory = ({ title, options }) => {
    return (
      <div key={title}>
        <h5 className="problem-tag-filter__category">{title}</h5>
        {options.map(opt => (
          <Checkbox
            key={opt.value}
            name={opt.value}
            className={classNames('problem-tag-filter__option', {
              'problem-tag-filter__option-child': this.isTagChild(opt.value),
            })}
            label={this.getTagName(opt) + ' (' + opt.count + ')'}
            checked={this.isTagSelected(opt.value)}
            indeterminate={this.isTagChildSelected(opt.value)}
            disabled={this.isTagParentSelected(opt.value)}
            onChange={this.changeTag}
          />
        ))}
      </div>
    );
  };

  isTagSelected = tag => {
    return this.state.tags.includes(tag);
  };

  isTagParentSelected = tag => {
    return this.state.tags.some(t => t !== tag && tag.startsWith(t));
  };

  isTagChildSelected = tag => {
    return this.state.tags.some(t => t !== tag && t.startsWith(tag));
  };

  isTagChild = tag => {
    return tag.includes(': ');
  };

  getTagName = opt => {
    return this.isTagChild(opt.value) ? opt.label.split(': ')[1] : opt.label;
  };

  changeTag = e => {
    const tag = e.target.name;
    const checked = e.target.checked;

    let tags = this.state.tags;
    if (checked) {
      tags = [...new Set([...tags, tag])]
        .filter(t => !(t !== tag && t.startsWith(tag)))
        .filter(t => !(t !== tag && tag.startsWith(t)));
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
