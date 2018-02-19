import { MenuItem } from '@blueprintjs/core';
import * as React from 'react';
import { push } from 'react-router-redux';
import { connect } from 'react-redux';

export interface MenuItemLinkProps {
  text: string;
  to: string;
}

interface MenuItemLinkConnectedProps {
  text: string;
  onClick: () => any;
}

const MenuItemLink = (props: MenuItemLinkConnectedProps) => <MenuItem text={props.text} onClick={props.onClick} />;

const mapDispatchToProps = {
  onClick: push,
};

const mergeProps = (stateProps, dispatchProps, ownProps: MenuItemLinkProps) => ({
  text: ownProps.text,
  onClick: () => dispatchProps.onClick(ownProps.to),
});

export default connect(undefined, mapDispatchToProps, mergeProps)(MenuItemLink);
