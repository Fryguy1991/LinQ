package com.chrisfry.linq


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Switch
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.chrisfry.linq.business.presenters.SettingsPresenter
import com.chrisfry.linq.business.presenters.interfaces.ISettingsPresenter
import com.chrisfry.linq.userinterface.App

/**
 * A simple [Fragment] subclass.
 *
 */
class SettingsFragment : Fragment(), SettingsPresenter.ISettingsView, View.OnClickListener,
    CompoundButton.OnCheckedChangeListener {
    companion object {
        private val TAG = this::class.java.name
    }

    private var presenter: ISettingsPresenter? = null

    // UI ELEMENTS
    private lateinit var enforceLinksLabel: View
    private lateinit var enforceLinksSwitch: Switch
    private lateinit var logoutLabel: View
    private lateinit var currentUserText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val parentActivity = activity
        if (parentActivity != null) {
            presenter = SettingsPresenter((parentActivity.application as App).appComponent)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        enforceLinksLabel = view.findViewById(R.id.tv_enforce_links_label)
        enforceLinksSwitch = view.findViewById(R.id.switch_enforce_links)

        logoutLabel = view.findViewById(R.id.tv_log_out_label)
        currentUserText = view.findViewById(R.id.tv_logged_in_as)

        presenter?.attach(this)

        addListeners()
    }

    private fun addListeners() {
        enforceLinksLabel.setOnClickListener(this)
        enforceLinksSwitch.setOnCheckedChangeListener(this)

        logoutLabel.setOnClickListener(this)
        currentUserText.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.tv_enforce_links_label -> {
                    enforceLinksSwitch.isChecked = !enforceLinksSwitch.isChecked
                }
                R.id.tv_log_out_label,
                R.id.tv_logged_in_as -> {
                    presenter?.logoutPressed()
                }
            }
        }
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        if (buttonView != null) {
            when (buttonView.id) {
                R.id.switch_enforce_links -> {
                    presenter?.enforceLinksChanged(isChecked)
                }
            }
        }
    }

    override fun onDestroy() {
        presenter?.detach()
        presenter = null

        super.onDestroy()
    }

    override fun displayEnforceLinks(shouldBeActive: Boolean) {
        enforceLinksSwitch.isChecked = shouldBeActive
    }

    override fun displayUser(userName: String) {
        var nameToDisplay = getString(R.string.invalid_user)
        if (userName.isNotEmpty()) {
            nameToDisplay = userName
        }

        currentUserText.text = String.format(getString(R.string.logged_in_as), nameToDisplay)
    }

    override fun navigateToLogin() {
        findNavController().navigate(SettingsFragmentDirections.actionSettingsFragmentToLoginFragment())
    }
}
