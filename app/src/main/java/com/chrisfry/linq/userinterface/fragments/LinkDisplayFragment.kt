package com.chrisfry.linq.userinterface.fragments


import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.chrisfry.linq.R
import com.chrisfry.linq.business.presenters.LinkDisplayPresenter
import com.chrisfry.linq.business.presenters.interfaces.ILinkDisplayPresenter
import com.chrisfry.linq.userinterface.App
import kaaes.spotify.webapi.android.models.Track

/**
 * A simple [Fragment] subclass.
 *
 */
class LinkDisplayFragment : Fragment(), LinkDisplayPresenter.ILinkDisplayView {

    // Reference to view presenter
    private var presenter: ILinkDisplayPresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        val parentActivity = activity
        if (parentActivity != null) {
            val app = parentActivity.application as App

            presenter = LinkDisplayPresenter(app.appComponent)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_link_display, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter?.attach(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.settingsFragment -> {
                presenter?.settingsPressed()
                return true
            }
            else -> {
                // Not handling menu item here
                return super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onDestroy() {
        presenter?.detach()
        presenter = null

        super.onDestroy()
    }

    override fun displayTrackGroups(trackGroupList: List<List<Track>>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun displayUndoTime() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun displaySettings() {
        findNavController().navigate(LinkDisplayFragmentDirections.actionLinkDisplayFragmentToSettingsFragment())
    }

    override fun navigateToNewTrackGroup() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun navigateToEditTrackGroup() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
