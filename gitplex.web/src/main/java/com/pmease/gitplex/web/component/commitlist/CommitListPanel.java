package com.pmease.gitplex.web.component.commitlist;

import java.util.List;

import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.request.resource.CssResourceReference;

import com.pmease.commons.git.Commit;
import com.pmease.gitplex.core.model.Repository;
import com.pmease.gitplex.web.Constants;
import com.pmease.gitplex.web.component.avatar.ContributorAvatars;
import com.pmease.gitplex.web.component.commitmessage.CommitMessagePanel;
import com.pmease.gitplex.web.component.contributionpanel.ContributionPanel;
import com.pmease.gitplex.web.component.hashandcode.HashAndCodePanel;

@SuppressWarnings("serial")
public class CommitListPanel extends Panel {

	private final IModel<Repository> repoModel;
	
	private final IModel<List<Commit>> commitsModel;
	
	public CommitListPanel(String id, IModel<Repository> repoModel, IModel<List<Commit>> commitsModel) {
		super(id);
		this.repoModel = repoModel;
		this.commitsModel = commitsModel;
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		
		String tooManyMessage = "Too many commits, displaying recent " + Constants.MAX_DISPLAY_COMMITS;
		add(new Label("tooManyCommits", tooManyMessage) {

			@Override
			protected void onConfigure() {
				super.onConfigure();

				setVisible(commitsModel.getObject().size()>Constants.MAX_DISPLAY_COMMITS);
			}
			
		});
		add(new ListView<Commit>("commits", new LoadableDetachableModel<List<Commit>>() {

			@Override
			protected List<Commit> load() {
				List<Commit> commits = commitsModel.getObject();
				if (commits.size() > Constants.MAX_DISPLAY_COMMITS)
					return commits.subList(commits.size()-Constants.MAX_DISPLAY_COMMITS, commits.size());
				else 
					return commits;
			}
			
		}) {

			@Override
			protected void populateItem(final ListItem<Commit> item) {
				Commit commit = item.getModelObject();
				
				item.add(new ContributorAvatars("avatar", commit.getAuthor(), commit.getCommitter()));

				item.add(new CommitMessagePanel("message", repoModel, new AbstractReadOnlyModel<Commit>() {

					@Override
					public Commit getObject() {
						return item.getModelObject();
					}
					
				}));

				item.add(new ContributionPanel("author", commit.getAuthor(), commit.getCommitter()));
				
				item.add(new HashAndCodePanel("hashAndCode", repoModel, commit.getHash()));
			}
			
		});
		
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		
		response.render(CssHeaderItem.forReference(
				new CssResourceReference(CommitListPanel.class, "commit-list.css")));
	}

	@Override
	protected void onDetach() {
		repoModel.detach();
		commitsModel.detach();
		
		super.onDetach();
	}

}
