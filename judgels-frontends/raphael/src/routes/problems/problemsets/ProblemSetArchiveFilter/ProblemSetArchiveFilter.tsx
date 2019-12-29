import classNames from 'classnames';
import { RadioGroup, Radio } from '@blueprintjs/core';
import { push } from 'connected-react-router';
import * as React from 'react';
import { connect } from 'react-redux';
import { withRouter, RouteComponentProps } from 'react-router';
import { parse, stringify } from 'query-string';

import { ContentCard } from '../../../../components/ContentCard/ContentCard';
import { ArchivesResponse, Archive } from '../../../../modules/api/jerahmeel/archive';
import { archiveActions as injectedArchiveActions } from '../modules/archiveActions';

import './ProblemSetArchiveFilter.css';

export interface ProblemSetArchiveFilterProps extends RouteComponentProps<{ archiveSlug: string }> {
  onGetArchives: () => Promise<ArchivesResponse>;
  onPush: (any) => any;
}

interface ProblemSetArchiveFilterState {
  archiveSlug?: string;
  response?: ArchivesResponse;
}

class ProblemSetArchiveFilter extends React.Component<ProblemSetArchiveFilterProps, ProblemSetArchiveFilterState> {
  state: ProblemSetArchiveFilterState = {};

  constructor(props) {
    super(props);

    const queries = parse(this.props.location.search);
    const archiveSlug = (queries.archiveSlug as string) || '';

    this.state = { archiveSlug };
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
        <h4>Select archive</h4>
        <hr />
        {this.renderArchives()}
      </ContentCard>
    );
  }

  private renderArchives = () => {
    const archives = [{ slug: '', name: '(all)' }, ...this.state.response.data];
    return (
      <RadioGroup onChange={this.changeArchive} selectedValue={this.state.archiveSlug}>
        {archives.map(archive => (
          <Radio key={archive.slug} labelElement={this.renderArchiveOption(archive)} value={archive.slug} />
        ))}
      </RadioGroup>
    );
  };

  private renderArchiveOption = (archive: Archive) => {
    return (
      <span className={classNames({ 'archive-filter__option--inactive': archive.slug !== this.state.archiveSlug })}>
        {archive.name}
      </span>
    );
  };

  private changeArchive = e => {
    const archiveSlug = e.target.value;
    const queries = parse(this.props.location.search);
    this.props.onPush({
      search: stringify({ ...queries, name: undefined, archiveSlug: archiveSlug === '' ? undefined : archiveSlug }),
    });
    this.setState({ archiveSlug });
  };
}

function createProblemSetArchiveFilter(archiveActions) {
  const mapDispatchToProps = {
    onGetArchives: archiveActions.getArchives,
    onPush: push,
  };
  return withRouter(connect(undefined, mapDispatchToProps)(ProblemSetArchiveFilter));
}

export default createProblemSetArchiveFilter(injectedArchiveActions);
